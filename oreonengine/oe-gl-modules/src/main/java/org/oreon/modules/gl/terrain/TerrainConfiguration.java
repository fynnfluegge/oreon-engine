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

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.model.Material;
import org.oreon.core.system.CommonConfig;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;
import org.oreon.modules.gl.gpgpu.NormalMapRenderer;
import org.oreon.modules.gl.terrain.fractals.FractalMap;

public class TerrainConfiguration {

	private float scaleY;
	private float scaleXZ;
	private int bezier;
	private int waterReflectionShift;
	private float sightRangeFactor;
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
	private List<Texture2D> splatmaps = new ArrayList<>();
	private List<FractalMap> fractals = new ArrayList<>();
	
	private int[] lod_range = new int[8];
	private int[] lod_morphing_area = new int[8];
	
	private GLShader shader;
	private GLShader gridShader;
	private GLShader shadowShader;
	
	public void loadFile(String file)
	{
		Properties properties = new Properties();
		try {
			InputStream stream = TerrainConfiguration.class.getClassLoader().getResourceAsStream(file);
			properties.load(stream);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setScaleY(Float.valueOf(properties.getProperty("scaleY")));
		setScaleXZ(Float.valueOf(properties.getProperty("scaleXZ")));
		
		if (!properties.getProperty("heightmap").equals("0")){
			setHeightmap(new Texture2D(properties.getProperty("heightmap")));
			getHeightmap().bind();
			getHeightmap().bilinearFilter();
				
			NormalMapRenderer normalRenderer = new NormalMapRenderer(getHeightmap().getWidth());
			normalRenderer.setStrength(Integer.valueOf(properties.getProperty("normalmap.strength")));
			normalRenderer.render(getHeightmap());
			setNormalmap(normalRenderer.getNormalmap());	
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
			
			Texture2D alphamap = new Texture2D(properties.getProperty("materials.material" + i + "_ALPHA"));
			alphamap.bind();
			alphamap.trilinearFilter();
			getMaterials().get(materials.size()-1).setAlphamap(alphamap);
			
			getMaterials().get(materials.size()-1).setHeightScaling(Float.valueOf(properties.getProperty("materials.material" + i + "_heightScaling")));
			getMaterials().get(materials.size()-1).setHorizontalScaling(Float.valueOf(properties.getProperty("materials.material" + i + "_horizontalScaling")));
		}

		setTessellationFactor(Integer.valueOf(properties.getProperty("tessellationFactor")));
		setTessellationSlope(Float.valueOf(properties.getProperty("tessellationSlope")));
		setTessellationShift(Float.valueOf(properties.getProperty("tessellationShift")));
		
		setTexDetail(Float.valueOf(properties.getProperty("texDetail")));
		
		sightRangeFactor = Float.valueOf(properties.getProperty("sightRangeFactor"));
		CommonConfig.getInstance().setSightRange(sightRangeFactor);
		
		setDetailRange(Integer.valueOf(properties.getProperty("detailRange")));
		
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
		setHeightmap(fractalMapGenerator.getFractalmap());
		
		NormalMapRenderer normalRenderer = new NormalMapRenderer(Constants.TERRAIN_FRACTALS_RESOLUTION);
		normalRenderer.setStrength(4);
		normalRenderer.render(getHeightmap());
		setNormalmap(normalRenderer.getNormalmap());
		
		SplatMapGenerator splatMapGenerator = new SplatMapGenerator(Constants.TERRAIN_FRACTALS_RESOLUTION);
		splatMapGenerator.render(getNormalmap());
		setSplatmap(splatMapGenerator.getSplatmap());
	}
	
	private int updateMorphingArea(int lod){
		return (int) ((scaleXZ/TerrainQuadtree.getRootPatches()) / (Math.pow(2, lod)));
	}
	
	public float getScaleY() {
		return scaleY;
	}
	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}
	public float getScaleXZ() {
		return scaleXZ;
	}
	public void setScaleXZ(float scaleXZ) {
		this.scaleXZ = scaleXZ;
	}
	public int getBezier() {
		return bezier;
	}
	public void setBezier(int bezier) {
		this.bezier = bezier;
	}
	public float getSightRangeFactor() {
		return sightRangeFactor;
	}
	public void setSightRangeFactor(float sightRangeFactor) {
		this.sightRangeFactor = sightRangeFactor;
	}
	public float getTexDetail() {
		return texDetail;
	}
	public void setTexDetail(float texDetail) {
		this.texDetail = texDetail;
	}
	public int getTessellationFactor() {
		return tessellationFactor;
	}
	public void setTessellationFactor(int tessellationFactor) {
		this.tessellationFactor = tessellationFactor;
	}
	public float getTessellationSlope() {
		return tessellationSlope;
	}
	public void setTessellationSlope(float tessellationSlope) {
		this.tessellationSlope = tessellationSlope;
	}
	public float getTessellationShift() {
		return tessellationShift;
	}
	public void setTessellationShift(float tessellationShift) {
		this.tessellationShift = tessellationShift;
	}
	public int getDetailRange() {
		return detailRange;
	}
	public void setDetailRange(int detailRange) {
		this.detailRange = detailRange;
	}
	public Texture2D getHeightmap() {
		return heightmap;
	}
	public void setHeightmap(Texture2D heightmap) {
		this.heightmap = heightmap;
	}
	public Texture2D getNormalmap() {
		return normalmap;
	}
	public void setNormalmap(Texture2D normalmap) {
		this.normalmap = normalmap;
	}
	public Texture2D getAmbientmap() {
		return ambientmap;
	}
	public void setAmbientmap(Texture2D ambientmap) {
		this.ambientmap = ambientmap;
	}

	public List<FractalMap> getFractals() {
		return fractals;
	}

	public GLShader getGridShader() {
		return gridShader;
	}

	public void setGridShader(GLShader gridShader) {
		this.gridShader = gridShader;
	}

	public void setLodRange(int index, int lod_range) {
		this.lod_range[index] = lod_range;
		lod_morphing_area[index] = lod_range - updateMorphingArea(index+1);
	}
	
	public int[] getLod_morphing_area() {
		return lod_morphing_area;
	}

	public GLShader getShadowShader() {
		return shadowShader;
	}

	public void setShadowShader(GLShader shadowShader) {
		this.shadowShader = shadowShader;
	}

	public GLShader getShader() {
		return shader;
	}

	public void setShader(GLShader shader) {
		this.shader = shader;
	}

	public int[] getLod_range() {
		return lod_range;
	}

	public int getWaterReflectionShift() {
		return waterReflectionShift;
	}

	public void setWaterReflectionShift(int waterReflectionShift) {
		this.waterReflectionShift = waterReflectionShift;
	}

	public List<Texture2D> getSplatmaps() {
		return splatmaps;
	}

	public void setSplatmaps(List<Texture2D> splatmaps) {
		this.splatmaps = splatmaps;
	}

	public FloatBuffer getHeightmapDataBuffer() {
		return heightmapDataBuffer;
	}

	public void setHeightmapDataBuffer(FloatBuffer heightmapDataBuffer) {
		this.heightmapDataBuffer = heightmapDataBuffer;
	}

	public List<Material> getMaterials() {
		return materials;
	}

	public void setMaterials(List<Material> materials) {
		this.materials = materials;
	}

	public Texture2D getSplatmap() {
		return splatmap;
	}

	public void setSplatmap(Texture2D splatmap) {
		this.splatmap = splatmap;
	}
}
