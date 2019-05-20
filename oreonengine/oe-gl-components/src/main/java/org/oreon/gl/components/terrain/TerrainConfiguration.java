package org.oreon.gl.components.terrain;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_GREEN;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glGetTexImage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.TextureImage2D;
import org.oreon.core.image.Image.SamplerFilter;
import org.oreon.core.image.Image.TextureWrapMode;
import org.oreon.core.math.Vec2f;
import org.oreon.core.model.Material;
import org.oreon.core.scenegraph.NodeComponent;
import org.oreon.core.util.BufferUtil;
import org.oreon.gl.components.terrain.fractals.FractalMap;
import org.oreon.gl.components.terrain.fractals.FractalMapGenerator;
import org.oreon.gl.components.util.NormalRenderer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerrainConfiguration extends NodeComponent{

	private float scaleY;
	private float scaleXZ;
	private int bezier;
	private int reflectionOffset;
	private float texDetail;
	private int tessellationFactor;
	private float tessellationSlope;
	private float tessellationShift;
	private int detailRange;
	private float normalStrength;
	private float heightStrength;
	private GLTexture heightmap;
	private GLTexture normalmap;
	private GLTexture ambientmap;
	private GLTexture splatmap;
	private FloatBuffer heightmapDataBuffer;
	private int heightmapResolution;
	private List<Material<GLTexture>> materials = new ArrayList<>();
	private int fractalMapResolution;
	private List<FractalMap> fractals = new ArrayList<>();
	
	private int[] lod_range = new int[8];
	private int[] lod_morphing_area = new int[8];
	private boolean diamond_square;
	
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
		normalStrength = Float.valueOf(properties.getProperty("normalmap.strength"));
		
		if (!properties.getProperty("heightmap").equals("0")){
			heightmap = new GLTexture(properties.getProperty("heightmap"));
			getHeightmap().bind();
			getHeightmap().bilinearFilter();
			
			heightStrength = Float.valueOf(properties.getProperty("heightmap.strength"));
			
			NormalRenderer normalRenderer = new NormalRenderer(getHeightmap().getMetaData().getWidth());
			normalRenderer.setStrength(normalStrength);
			normalRenderer.render(getHeightmap());
			normalmap = normalRenderer.getNormalmap();	
			createHeightmapDataBuffer();
		}
		
		for (int i=0; i<Integer.valueOf(properties.getProperty("materials.count")); i++){
			
			getMaterials().add(new Material<GLTexture>());
			
			GLTexture diffusemap = new TextureImage2D(properties.getProperty("materials.material" + i + "_DIF"),
					SamplerFilter.Trilinear, TextureWrapMode.None);
			getMaterials().get(materials.size()-1).setDiffusemap(diffusemap);
			
			GLTexture normalmap = new TextureImage2D(properties.getProperty("materials.material" + i + "_NRM"),
					SamplerFilter.Trilinear, TextureWrapMode.None);
			getMaterials().get(materials.size()-1).setNormalmap(normalmap);
			
			GLTexture heightmap = new TextureImage2D(properties.getProperty("materials.material" + i + "_DISP"),
					SamplerFilter.Trilinear, TextureWrapMode.None);
			getMaterials().get(materials.size()-1).setHeightmap(heightmap);
			
			getMaterials().get(materials.size()-1).setHeightScaling(Float.valueOf(properties.getProperty("materials.material" + i + "_heightScaling")));
			getMaterials().get(materials.size()-1).setHorizontalScaling(Float.valueOf(properties.getProperty("materials.material" + i + "_horizontalScaling")));
		}

		tessellationFactor = Integer.valueOf(properties.getProperty("tessellationFactor"));
		tessellationSlope = Float.valueOf(properties.getProperty("tessellationSlope"));
		tessellationShift = Float.valueOf(properties.getProperty("tessellationShift"));
		
		texDetail = Float.valueOf(properties.getProperty("texDetail"));
		
		detailRange = Integer.valueOf(properties.getProperty("detailRange"));
		
		diamond_square = Integer.valueOf(properties.getProperty("diamond_square")) == 1 ? true : false; 
		
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
		heightmapResolution = Integer.valueOf(properties.getProperty("heightmap.resolution"));
		
		for (int i=0; i<fractalCount; i++){
			
			int L = Integer.valueOf(properties.getProperty("fractal" + i + ".L"));
			float amplitude = Float.valueOf(properties.getProperty("fractal" + i + ".amplitude"));
			float capillar = Float.valueOf(properties.getProperty("fractal" + i + ".capillar"));;
			int scaling = Integer.valueOf(properties.getProperty("fractal" + i + ".scaling"));
			float heightStrength = Float.valueOf(properties.getProperty("fractal" + i + ".heightStrength"));
			float horizontalStrength = Float.valueOf(properties.getProperty("fractal" + i + ".horizontalStrength"));
			float normalStrength = Float.valueOf(properties.getProperty("fractal" + i + ".normalStrength"));
			int random = Integer.valueOf(properties.getProperty("fractal" + i + ".random"));
			Vec2f direction = new Vec2f(Float.valueOf(properties.getProperty("fractal" + i + ".direction.x")),
					Float.valueOf(properties.getProperty("fractal" + i + ".direction.y")));
			float intensity = Float.valueOf(properties.getProperty("fractal" + i + ".intensity"));
			float alignment = Float.valueOf(properties.getProperty("fractal" + i + ".alignment"));
			boolean choppy = Integer.valueOf(properties.getProperty("fractal" + i + ".choppy")) == 1 ? true : false;
			
			FractalMap fractal = new FractalMap(fractalMapResolution, L, amplitude,
					direction, intensity, capillar, alignment, choppy, 
					scaling, heightStrength, horizontalStrength, normalStrength, random);
			fractal.render();
			
			getFractals().add(fractal);
		}
		
		renderFractalMap();
		createHeightmapDataBuffer();
	}
	
	public void createHeightmapDataBuffer(){
		
		heightmapDataBuffer = BufferUtil.createFloatBuffer(getHeightmap().getMetaData().getWidth() * getHeightmap().getMetaData().getHeight());
		heightmap.bind();
		// GL_GREEN since y-space (height) stored in green channel
		glGetTexImage(GL_TEXTURE_2D, 0, GL_GREEN, GL_FLOAT, heightmapDataBuffer);
	}
	
	public void renderFractalMap(){
		
		FractalMapGenerator fractalMapGenerator = new FractalMapGenerator(heightmapResolution);
		fractalMapGenerator.render(fractals);
		heightmap = fractalMapGenerator.getHeightmap();
//		heightmap = fractals.get(2).getHeightmap();
		
//		fractalMapGenerator.renderNormalmap(fractals);
		normalmap = fractalMapGenerator.getNormalmap();
//		normalmap = fractals.get(2).getNormalmap();
		
		SplatMapGenerator splatMapGenerator = new SplatMapGenerator(heightmapResolution);
		splatmap = splatMapGenerator.getSplatmap();
		splatMapGenerator.render(getNormalmap(), getHeightmap(), getScaleY());
	}
	
	private int updateMorphingArea(int lod){
		return (int) ((scaleXZ/TerrainQuadtree.getRootPatches()) / (Math.pow(2, lod)));
	}

	public void setLodRange(int index, int lod_range) {
		this.lod_range[index] = lod_range;
		lod_morphing_area[index] = lod_range - updateMorphingArea(index+1);
	}
	
}
