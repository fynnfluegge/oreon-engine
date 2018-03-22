package org.oreon.modules.gl.terrain;

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

import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.model.Material;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;
import org.oreon.modules.gl.gpgpu.NormalMapRenderer;
import org.oreon.modules.gl.terrain.fractals.FractalMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TerrainConfiguration {

	private float scaleY;
	private float scaleXZ;
	private int bezier;
	private int waterReflectionShift;
	private float texDetail;
	private int tessellationFactor;
	private float tessellationSlope;
	private float tessellationShift;
	private int detailRange;
	private Texture2D heightmap;
	private Texture2D normalmap;
	private Texture2D ambientmap;
	private Texture2D splatmap;
	private FloatBuffer heightmapDataBuffer;
	private List<Material> materials = new ArrayList<>();
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
			heightmap = new Texture2D(properties.getProperty("heightmap"));
			getHeightmap().bind();
			getHeightmap().bilinearFilter();
			
			NormalMapRenderer normalRenderer = new NormalMapRenderer(getHeightmap().getWidth());
			normalRenderer.setStrength(Integer.valueOf(properties.getProperty("normalmap.strength")));
			normalRenderer.render(getHeightmap());
			normalmap = normalRenderer.getNormalmap();	
			createHeightmapDataBuffer();
		}
		
		for (int i=0; i<Integer.valueOf(properties.getProperty("materials.count")); i++){
			
			getMaterials().add(new Material());
			
			Texture2D diffusemap = new Texture2D(properties.getProperty("materials.material" + i + "_DIF"));
			diffusemap.bind();
			diffusemap.trilinearFilter();
			getMaterials().get(materials.size()-1).setDiffusemap(diffusemap);
			
			Texture2D normalmap = new Texture2D(properties.getProperty("materials.material" + i + "_NRM"));
			normalmap.bind();
			normalmap.trilinearFilter();
			getMaterials().get(materials.size()-1).setNormalmap(normalmap);
			
			Texture2D heightmap = new Texture2D(properties.getProperty("materials.material" + i + "_DISP"));
			heightmap.bind();
			heightmap.trilinearFilter();
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
		
		for (int i=0; i<Integer.valueOf(properties.getProperty("fractals.count")); i++){
			
			float amp = Float.valueOf(properties.getProperty("fractal" + i + ".amp"));
			float l = Float.valueOf(properties.getProperty("fractal" + i + ".l"));;
			int scaling = Integer.valueOf(properties.getProperty("fractal" + i + ".scaling"));;
			float strength = Float.valueOf(properties.getProperty("fractal" + i + ".strength"));
			int random = Integer.valueOf(properties.getProperty("fractal" + i + ".random"));
			
			FractalMap fractal = new FractalMap(Constants.TERRAIN_FRACTALS_RESOLUTION,amp,l,scaling,strength,random);
			getFractals().add(fractal);
		}
		
		renderFractalMap();
		createHeightmapDataBuffer();
	}
	
	public void createHeightmapDataBuffer(){
		
		heightmapDataBuffer = BufferUtil.createFloatBuffer(getHeightmap().getWidth() * getHeightmap().getHeight());
		heightmap.bind();
		glGetTexImage(GL_TEXTURE_2D,0,GL_RED,GL_FLOAT,heightmapDataBuffer);
	}
	
	public void renderFractalMap(){
		
		FractalMapGenerator fractalMapGenerator = new FractalMapGenerator(Constants.TERRAIN_FRACTALS_RESOLUTION);
		fractalMapGenerator.render(fractals);
		heightmap = fractalMapGenerator.getFractalmap();
		
		NormalMapRenderer normalRenderer = new NormalMapRenderer(Constants.TERRAIN_FRACTALS_RESOLUTION);
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
