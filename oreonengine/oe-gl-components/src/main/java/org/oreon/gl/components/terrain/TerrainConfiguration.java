package org.oreon.gl.components.terrain;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glGetTexImage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DTrilinearFilter;
import org.oreon.core.math.Vec2f;
import org.oreon.core.model.Material;
import org.oreon.core.scenegraph.NodeComponent;
import org.oreon.core.util.BufferUtil;
import org.oreon.gl.components.gpgpu.NormalMapRenderer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerrainConfiguration extends NodeComponent{

	private float scaleY;
	private float scaleXZ;
	private int bezier;
	private int waterReflectionShift;
	private float texDetail;
	private int tessellationFactor;
	private float tessellationSlope;
	private float tessellationShift;
	private int detailRange;
	private GLTexture heightmap;
	private GLTexture normalmap;
	private GLTexture ambientmap;
	private GLTexture splatmap;
	private FloatBuffer heightmapDataBuffer;
	private List<Material<GLTexture>> materials = new ArrayList<>();
	private int fractalMapResolution;
	private List<FractalMap> fractals = new ArrayList<>();
	
	private int[] lod_range = new int[8];
	private int[] lod_morphing_area = new int[8];
	
	public TerrainConfiguration() {
		
		Properties properties = new Properties();
		try {
			InputStream stream = TerrainConfiguration.class.getClassLoader().getResourceAsStream("terrain-config.properties");
			properties.load(stream);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		scaleY = Float.valueOf(properties.getProperty("scaleY"));
		scaleXZ = Float.valueOf(properties.getProperty("scaleXZ"));
		
		if (!properties.getProperty("heightmap").equals("0")){
			heightmap = new GLTexture(properties.getProperty("heightmap"));
			getHeightmap().bind();
			getHeightmap().bilinearFilter();
			
			NormalMapRenderer normalRenderer = new NormalMapRenderer(getHeightmap().getMetaData().getWidth());
			normalRenderer.setStrength(Integer.valueOf(properties.getProperty("normalmap.strength")));
			normalRenderer.render(getHeightmap());
			normalmap = normalRenderer.getNormalmap();	
			createHeightmapDataBuffer();
		}
		
		for (int i=0; i<Integer.valueOf(properties.getProperty("materials.count")); i++){
			
			getMaterials().add(new Material<GLTexture>());
			
			GLTexture diffusemap = new Texture2DTrilinearFilter(properties.getProperty("materials.material" + i + "_DIF"));
			getMaterials().get(materials.size()-1).setDiffusemap(diffusemap);
			
			GLTexture normalmap = new Texture2DTrilinearFilter(properties.getProperty("materials.material" + i + "_NRM"));
			getMaterials().get(materials.size()-1).setNormalmap(normalmap);
			
			GLTexture heightmap = new Texture2DTrilinearFilter(properties.getProperty("materials.material" + i + "_DISP"));
			getMaterials().get(materials.size()-1).setHeightmap(heightmap);
			
			getMaterials().get(materials.size()-1).setHeightScaling(Float.valueOf(properties.getProperty("materials.material" + i + "_heightScaling")));
			getMaterials().get(materials.size()-1).setHorizontalScaling(Float.valueOf(properties.getProperty("materials.material" + i + "_horizontalScaling")));
		}

		tessellationFactor = Integer.valueOf(properties.getProperty("tessellationFactor"));
		tessellationSlope = Float.valueOf(properties.getProperty("tessellationSlope"));
		tessellationShift = Float.valueOf(properties.getProperty("tessellationShift"));
		
		texDetail = Float.valueOf(properties.getProperty("texDetail"));
		
		detailRange = Integer.valueOf(properties.getProperty("detailRange"));
		
		for (int i=0; i<Integer.valueOf(properties.getProperty("lod.count")); i++){
			
			if (Integer.valueOf(properties.getProperty("lodRanges.lod" + i)) == 0){
				lod_range[i] = 0;
				lod_morphing_area[i] = 0;
			}
			else {
				setLodRange(i, Integer.valueOf(properties.getProperty("lodRanges.lod" + i)));
			}
		}
		
		int fractalCount = Integer.valueOf(properties.getProperty("fractals.count"));
		fractalMapResolution = Integer.valueOf(properties.getProperty("fractals.resolution"));
		
		for (int i=0; i<fractalCount; i++){
			
			int L = Integer.valueOf(properties.getProperty("fractal" + i + ".L"));
			float amplitude = Float.valueOf(properties.getProperty("fractal" + i + ".amplitude"));
			float capillarSuppression = Float.valueOf(properties.getProperty("fractal" + i + ".capillarSuppression"));;
			int scaling = Integer.valueOf(properties.getProperty("fractal" + i + ".scaling"));
			float strength = Float.valueOf(properties.getProperty("fractal" + i + ".strength"));
			int random = Integer.valueOf(properties.getProperty("fractal" + i + ".random"));
			Vec2f direction = new Vec2f(Float.valueOf(properties.getProperty("fractal" + i + ".direction.x")),
					Float.valueOf(properties.getProperty("fractal" + i + ".direction.y")));
			float intensity = Float.valueOf(properties.getProperty("fractal" + i + ".intensity"));
			FractalMap fractal = new FractalMap(fractalMapResolution, L, amplitude,
					direction, intensity, capillarSuppression, scaling, strength, random);
			getFractals().add(fractal);
		}
		
		renderFractalMap();
		createHeightmapDataBuffer();
	}
	
	public void createHeightmapDataBuffer(){
		
		heightmapDataBuffer = BufferUtil.createFloatBuffer(getHeightmap().getMetaData().getWidth() * getHeightmap().getMetaData().getHeight());
		heightmap.bind();
		glGetTexImage(GL_TEXTURE_2D,0,GL_RED,GL_FLOAT,heightmapDataBuffer);
	}
	
	public void renderFractalMap(){
		
		FractalMapGenerator fractalMapGenerator = new FractalMapGenerator(fractalMapResolution);
		fractalMapGenerator.render(fractals);
		heightmap = fractalMapGenerator.getFractalmap();
		
		NormalMapRenderer normalRenderer = new NormalMapRenderer(fractalMapResolution);
		normalRenderer.setStrength(8);
		normalRenderer.render(getHeightmap());
		normalmap = normalRenderer.getNormalmap();
		
		SplatMapGenerator splatMapGenerator = new SplatMapGenerator(2048);
		splatMapGenerator.render(getNormalmap());
		splatmap = splatMapGenerator.getSplatmap();
	}
	
	private int updateMorphingArea(int lod){
		return (int) ((scaleXZ/TerrainQuadtree.getRootPatches()) / (Math.pow(2, lod)));
	}

	public void setLodRange(int index, int lod_range) {
		this.lod_range[index] = lod_range;
		lod_morphing_area[index] = lod_range - updateMorphingArea(index+1);
	}
	
}
