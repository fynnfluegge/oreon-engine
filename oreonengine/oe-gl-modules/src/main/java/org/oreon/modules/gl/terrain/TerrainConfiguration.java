package org.oreon.modules.gl.terrain;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glGetTexImage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.model.Material;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;
import org.oreon.core.util.Util;
import org.oreon.modules.gl.gpgpu.NormalMapRenderer;
import org.oreon.modules.gl.terrain.fractals.FractalMaps;

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
	private FloatBuffer heightmapDataBuffer;
	private List<Material> materials = new ArrayList<>();
//	private Material material0;
//	private Material material1;
//	private Material material2;
//	private Material material3;
	private List<Texture2D> splatmaps = new ArrayList<>();
	private List<FractalMaps> fractals = new ArrayList<>();
	
	private int[] lod_range = new int[8];
	private int[] lod_morphing_area = new int[8];
	
	private GLShader shader;
	private GLShader gridShader;
	private GLShader shadowShader;
	
	public void saveToFile()
	{
		File file = new File("./res/editor/terrain_settings.txt");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write("#textures");
			writer.newLine();
			writer.write("#terrain mesh settings");
			writer.newLine();
			writer.write("scaleXZ " + scaleXZ);
			writer.newLine();
			writer.write("scaleY " + scaleY);
			writer.newLine();
			writer.write("texDetail " + texDetail);
			writer.newLine();
			writer.write("tessellationFactor " + tessellationFactor);
			writer.newLine();
			writer.write("tessellationSlope " + tessellationSlope);
			writer.newLine();
			writer.write("tessellationShift " + tessellationShift);
			writer.newLine();
			writer.write("detailRange " + detailRange);
			writer.newLine();
			writer.write("sightRangeFactor " + sightRangeFactor);
			writer.newLine();
			writer.write("#lod ranges");
			writer.newLine();
			int i = 1;
			for (int lod_range : lod_range){
				writer.write("lod" + i + "_range " + lod_range);
				writer.newLine();
				i++;
			}
			i = 0;
			for (FractalMaps fractal : fractals){
				writer.write("fractal_stage" + i);
				writer.newLine();
				writer.write("amp " + fractal.getAmplitude());
				writer.newLine();
				writer.write("l " + fractal.getL());
				writer.newLine();
				writer.write("scaling " + fractal.getScaling());
				writer.newLine();
				writer.write("strength " + fractal.getStrength());
				writer.newLine();
				writer.write("normalStrength " + fractal.getNormalStrength());
				writer.newLine();
				writer.write("random " + fractal.getRandom());
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(file.canWrite());
	}
	
	public void loadFile(String file)
	{
		BufferedReader reader = null;
		InputStream is = TerrainConfiguration.class.getClassLoader().getResourceAsStream(file);
		
		try{
				reader = new BufferedReader(new InputStreamReader(is));
				String line;
				
				while((line = reader.readLine()) != null){
					
					String[] tokens = line.split(" ");
					tokens = Util.removeEmptyStrings(tokens);
					
					if(tokens.length == 0)
						continue;
					if(tokens[0].equals("scaleY")){
						setScaleY(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("scaleXZ")){
						setScaleXZ(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("heightmap")){
						setHeightmap(new Texture2D(tokens[1]));
						getHeightmap().bind();
						getHeightmap().bilinearFilter();
						
						NormalMapRenderer normalRenderer = new NormalMapRenderer(getHeightmap().getWidth());
						normalRenderer.setStrength(Integer.valueOf(tokens[2]));
						normalRenderer.render(getHeightmap());
						setNormalmap(normalRenderer.getNormalmap());
						
						createHeightmapDataBuffer();
					}
					if(tokens[0].equals("normalmap")){
						setNormalmap(new Texture2D(tokens[1]));
						getNormalmap().bind();
						getNormalmap().trilinearFilter();
					}
					if(tokens[0].equals("ambientmap")){
						setAmbientmap(new Texture2D(tokens[1]));
						getAmbientmap().bind();
						getAmbientmap().trilinearFilter();
					}
					if(tokens[0].equals("material")){
						getMaterials().add(new Material());
					}
					if(tokens[0].equals("material_DIF")){
						Texture2D diffusemap = new Texture2D(tokens[1]);
						diffusemap.bind();
						diffusemap.trilinearFilter();
						getMaterials().get(materials.size()-1).setDiffusemap(diffusemap);
					}
					if(tokens[0].equals("material_NRM")){
						Texture2D normalmap = new Texture2D(tokens[1]);
						normalmap.bind();
						normalmap.trilinearFilter();
						getMaterials().get(materials.size()-1).setNormalmap(normalmap);
					}
					if(tokens[0].equals("material_DISP")){
						Texture2D heightmap = new Texture2D(tokens[1]);
						heightmap.bind();
						heightmap.trilinearFilter();
						getMaterials().get(materials.size()-1).setHeightmap(heightmap);
					}
					if(tokens[0].equals("material_ALPHA")){
						Texture2D alphamap = new Texture2D(tokens[1]);
						alphamap.bind();
						alphamap.trilinearFilter();
						getMaterials().get(materials.size()-1).setAlphamap(alphamap);
					}
					if(tokens[0].equals("material_heightScaling")){
						getMaterials().get(materials.size()-1).setHeightScaling(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material_horizontalScaling")){
						getMaterials().get(materials.size()-1).setHorizontalScaling(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material_emission")){
						getMaterials().get(materials.size()-1).setEmission(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material_shininess")){
						getMaterials().get(materials.size()-1).setShininess(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("tessellationFactor")){
						setTessellationFactor(Integer.valueOf(tokens[1]));
					}
					if(tokens[0].equals("tessellationSlope")){
						setTessellationSlope(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("tessellationShift")){
						setTessellationShift(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("texDetail")){
						setTexDetail(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("sightRangeFactor")){
						sightRangeFactor = Float.valueOf(tokens[1]);
						CoreSystem.getInstance().getRenderingEngine().setSightRangeFactor(sightRangeFactor);
					}
					if(tokens[0].equals("bezier")){
						setBezier(Integer.valueOf(tokens[1]));
					}
					if(tokens[0].equals("detailRange")){
						setDetailRange(Integer.valueOf(tokens[1]));
					}
					if (tokens[0].equals("#lod_ranges")){					
						for (int i = 0; i < 8; i++){
							line = reader.readLine();
							tokens = line.split(" ");
							tokens = Util.removeEmptyStrings(tokens);
							if (tokens[0].equals("lod" + (i+1) + "_range")){
								if (Integer.valueOf(tokens[1]) == 0){
									lod_range[i] = 0;
									lod_morphing_area[i] = 0;
								}
								else {
									setLodRange(i, Integer.valueOf(tokens[1]));
								}
							}
						}
					}
					if(tokens[0].equals("fractal_stage0")){
						loadFractalMap(reader);
					}	
					if(tokens[0].equals("fractal_stage1")){
						loadFractalMap(reader);
					}	
					if(tokens[0].equals("fractal_stage2")){
						loadFractalMap(reader);
					}	
					if(tokens[0].equals("fractal_stage3")){
						loadFractalMap(reader);
					}	
					if(tokens[0].equals("fractal_stage4")){
						loadFractalMap(reader);
					}	
					if(tokens[0].equals("fractal_stage5")){
						loadFractalMap(reader);
					}	
					if(tokens[0].equals("fractal_stage6")){
						loadFractalMap(reader);
					}	
					if(tokens[0].equals("fractal_stage7")){
						loadFractalMap(reader);
					}	
					if(tokens[0].equals("fractal_stage8")){
						loadFractalMap(reader);
					}	
					if(tokens[0].equals("fractal_stage9")){
						loadFractalMap(reader);
					}	
				}
				reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void loadFractalMap(BufferedReader reader){
		
		String line;
		String[] tokens;
		
		float amp = 0;
		float l = 0;
		int scaling = 0;
		float strength = 0;
		int random = 0;
		int normalStrength = 0;
		
		try{
			line = reader.readLine();
			tokens = line.split(" ");
			tokens = Util.removeEmptyStrings(tokens);
			if(tokens[0].equals("amp"))
				amp = Float.valueOf(tokens[1]);
			line = reader.readLine();
			tokens = line.split(" ");
			tokens = Util.removeEmptyStrings(tokens);
			if(tokens[0].equals("l"))
				l = Float.valueOf(tokens[1]);
			line = reader.readLine();
			tokens = line.split(" ");
			tokens = Util.removeEmptyStrings(tokens);
			if(tokens[0].equals("scaling"))
				scaling = Integer.valueOf(tokens[1]);
			line = reader.readLine();
			tokens = line.split(" ");
			tokens = Util.removeEmptyStrings(tokens);
			if(tokens[0].equals("strength"))
				strength = Float.valueOf(tokens[1]);
			line = reader.readLine();
			tokens = line.split(" ");
			tokens = Util.removeEmptyStrings(tokens);
			if(tokens[0].equals("normalStrength"))
				normalStrength = Integer.valueOf(tokens[1]);
			line = reader.readLine();
			tokens = line.split(" ");
			tokens = Util.removeEmptyStrings(tokens);
			if(tokens[0].equals("random")){
				if (tokens.length == 2)
					random = Integer.valueOf(tokens[1]);
				else
					random = new Random().nextInt(1000);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		FractalMaps fractal = new FractalMaps(Constants.TERRAIN_FRACTALS_RESOLUTION,amp,l,scaling,strength,normalStrength,random);
		getFractals().add(fractal);
	}
	
	private void createHeightmapDataBuffer(){
		
		heightmapDataBuffer = BufferUtil.createFloatBuffer(getHeightmap().getWidth() * getHeightmap().getHeight());
		heightmap.bind();
		glGetTexImage(GL_TEXTURE_2D,0,GL_RED,GL_FLOAT,heightmapDataBuffer);
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
//	public Material getMaterial1() {
//		return material1;
//	}
//	public void setMaterial1(Material material1) {
//		this.material1 = material1;
//	}
//	public Material getMaterial2() {
//		return material2;
//	}
//	public void setMaterial2(Material material2) {
//		this.material2 = material2;
//	}
//	public Material getMaterial3() {
//		return material3;
//	}
//	public void setMaterial3(Material material3) {
//		this.material3 = material3;
//	}

	public List<FractalMaps> getFractals() {
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

//	public Material getMaterial0() {
//		return material0;
//	}
//
//	public void setMaterial0(Material material0) {
//		this.material0 = material0;
//	}

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
}
