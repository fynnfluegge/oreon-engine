package modules.terrain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import modules.terrain.fractals.FractalMaps;
import engine.scenegraph.components.Material;
import engine.shader.Shader;
import engine.textures.Texture2D;
import engine.utils.Constants;
import engine.utils.Util;

public class TerrainConfiguration {

	private float scaleY;
	private float scaleXZ;
	private int bezíer;
	private int isWaterReflected;
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
	private Material material0;
	private Material material1;
	private Material material2;
	private Material material3;
	private ArrayList<FractalMaps> fractals = new ArrayList<FractalMaps>();
	
	private int[] lod_range = new int[8];
	private int[] lod_morphing_area = new int[8];
	
	private Shader shader;
	private Shader gridShader;
	private Shader shadowShader;
	
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
		
		try{
			if(new File(file).exists()){
				reader = new BufferedReader(new FileReader(file));
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
					if(tokens[0].equals("splatmap")){
						setSplatmap(new Texture2D(tokens[1]));
						getSplatmap().bind();
						getSplatmap().trilinearFilter();
					}
					if(tokens[0].equals("material0_DIF")){
						setMaterial0(new Material());
						getMaterial0().setDiffusemap(new Texture2D(tokens[1]));
						getMaterial0().getDiffusemap().bind();
						getMaterial0().getDiffusemap().trilinearFilter();
					}
					if(tokens[0].equals("material0_NRM")){
						getMaterial0().setNormalmap(new Texture2D(tokens[1]));
						getMaterial0().getNormalmap().bind();
						getMaterial0().getNormalmap().trilinearFilter();
					}
					if(tokens[0].equals("material0_DISP")){
						getMaterial0().setDisplacemap(new Texture2D(tokens[1]));
						getMaterial0().getDisplacemap().bind();
						getMaterial0().getDisplacemap().trilinearFilter();
					}
					if(tokens[0].equals("material0_displaceScale")){
						getMaterial0().setDisplaceScale(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material0_emission")){
						getMaterial0().setEmission(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material0_shininess")){
						getMaterial0().setShininess(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material1_DIF")){
						setMaterial1(new Material());
						getMaterial1().setDiffusemap(new Texture2D(tokens[1]));
						getMaterial1().getDiffusemap().bind();
						getMaterial1().getDiffusemap().trilinearFilter();
					}
					if(tokens[0].equals("material1_NRM")){
						getMaterial1().setNormalmap(new Texture2D(tokens[1]));
						getMaterial1().getNormalmap().bind();
						getMaterial1().getNormalmap().trilinearFilter();
					}
					if(tokens[0].equals("material1_DISP")){
						getMaterial1().setDisplacemap(new Texture2D(tokens[1]));
						getMaterial1().getDisplacemap().bind();
						getMaterial1().getDisplacemap().trilinearFilter();
					}
					if(tokens[0].equals("material1_displaceScale")){
						getMaterial1().setDisplaceScale(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material1_emission")){
						getMaterial1().setEmission(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material1_shininess")){
						getMaterial1().setShininess(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material2_DIF")){
						setMaterial2(new Material());
						getMaterial2().setDiffusemap(new Texture2D(tokens[1]));
						getMaterial2().getDiffusemap().bind();
						getMaterial2().getDiffusemap().trilinearFilter();
					}
					if(tokens[0].equals("material2_NRM")){
						getMaterial2().setNormalmap(new Texture2D(tokens[1]));
						getMaterial2().getNormalmap().bind();
						getMaterial2().getNormalmap().trilinearFilter();
					}
					if(tokens[0].equals("material2_DISP")){
						getMaterial2().setDisplacemap(new Texture2D(tokens[1]));
						getMaterial2().getDisplacemap().bind();
						getMaterial2().getDisplacemap().trilinearFilter();
					}
					if(tokens[0].equals("material2_displaceScale")){
						getMaterial2().setDisplaceScale(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material2_emission")){
						getMaterial2().setEmission(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material2_shininess")){
						getMaterial2().setShininess(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material3_DIF")){
						setMaterial3(new Material());
						getMaterial3().setDiffusemap(new Texture2D(tokens[1]));
						getMaterial3().getDiffusemap().bind();
						getMaterial3().getDiffusemap().trilinearFilter();
					}
					if(tokens[0].equals("material3_NRM")){
						getMaterial3().setNormalmap(new Texture2D(tokens[1]));
						getMaterial3().getNormalmap().bind();
						getMaterial3().getNormalmap().trilinearFilter();
					}
					if(tokens[0].equals("material3_DISP")){
						getMaterial3().setDisplacemap(new Texture2D(tokens[1]));
						getMaterial3().getDisplacemap().bind();
						getMaterial3().getDisplacemap().trilinearFilter();
					}
					if(tokens[0].equals("material3_displaceScale")){
						getMaterial3().setDisplaceScale(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material3_emission")){
						getMaterial3().setEmission(Float.valueOf(tokens[1]));
					}
					if(tokens[0].equals("material3_shininess")){
						getMaterial3().setShininess(Float.valueOf(tokens[1]));
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
					}
					if(tokens[0].equals("bezier")){
						setBezíer(Integer.valueOf(tokens[1]));
					}
					if(tokens[0].equals("detailRange")){
						setDetailRange(Integer.valueOf(tokens[1]));
					}
					if(tokens[0].equals("lod1_range")){
						setLod1_range( Integer.valueOf(tokens[1]));
					}
					if(tokens[0].equals("lod2_range")){
						setLod2_range( Integer.valueOf(tokens[1]));;
					}
					if(tokens[0].equals("lod3_range")){
						setLod3_range( Integer.valueOf(tokens[1]));
					}
					if(tokens[0].equals("lod4_range")){
						setLod4_range( Integer.valueOf(tokens[1]));
					}
					if(tokens[0].equals("lod5_range")){
						setLod5_range( Integer.valueOf(tokens[1]));
					}
					if(tokens[0].equals("lod6_range")){
						setLod6_range( Integer.valueOf(tokens[1]));
					}
					if(tokens[0].equals("lod7_range")){
						setLod7_range( Integer.valueOf(tokens[1]));
					}
					if(tokens[0].equals("lod8_range")){
						setLod8_range( Integer.valueOf(tokens[1]));
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
	
	public void ReloadFractals(String file){
		
		getFractals().clear();
		BufferedReader reader = null;
		
		try{
			if(new File(file).exists()){
				reader = new BufferedReader(new FileReader(file));
				String line;
				
				while((line = reader.readLine()) != null){
					
					String[] tokens = line.split(" ");
					tokens = Util.removeEmptyStrings(tokens);
					
					if(tokens.length == 0)
						continue;
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
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
	}
	
	private int updateMorphingArea(int lod){
		return (int) ((6000/TerrainQuadtree.getRootPatches()) / (Math.pow(2, lod)));
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
	public int getBezíer() {
		return bezíer;
	}
	public void setBezíer(int bezíer) {
		this.bezíer = bezíer;
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
	public Texture2D getSplatmap() {
		return splatmap;
	}
	public void setSplatmap(Texture2D splatmap) {
		this.splatmap = splatmap;
	}
	public Material getMaterial1() {
		return material1;
	}
	public void setMaterial1(Material material1) {
		this.material1 = material1;
	}
	public Material getMaterial2() {
		return material2;
	}
	public void setMaterial2(Material material2) {
		this.material2 = material2;
	}
	public Material getMaterial3() {
		return material3;
	}
	public void setMaterial3(Material material3) {
		this.material3 = material3;
	}

	public ArrayList<FractalMaps> getFractals() {
		return fractals;
	}

	public Shader getGridShader() {
		return gridShader;
	}

	public void setGridShader(Shader gridShader) {
		this.gridShader = gridShader;
	}

	public void setLod1_range(int lod1_range) {
		this.lod_range[0] = lod1_range;
		lod_morphing_area[0] = lod1_range-updateMorphingArea(1);
	}

	public void setLod2_range(int lod2_range) {
		this.lod_range[1] = lod2_range;
		lod_morphing_area[1] = lod2_range-updateMorphingArea(2);
	}

	public void setLod3_range(int lod3_range) {
		this.lod_range[2] = lod3_range;
		lod_morphing_area[2] = lod3_range-updateMorphingArea(3);
	}

	public void setLod4_range(int lod4_range) {
		this.lod_range[3] = lod4_range;
		lod_morphing_area[3] = lod4_range-updateMorphingArea(4);
	}

	public void setLod5_range(int lod5_range) {
		this.lod_range[4] = lod5_range;
		lod_morphing_area[4] = lod5_range-updateMorphingArea(5);
	}

	public void setLod6_range(int lod6_range) {
		this.lod_range[5] = lod6_range;
		lod_morphing_area[5] = lod6_range-updateMorphingArea(6);
	}

	public void setLod7_range(int lod7_range) {
		this.lod_range[6] = lod7_range;
		lod_morphing_area[6] = lod7_range-updateMorphingArea(7);
	}

	public void setLod8_range(int lod8_range) {
		this.lod_range[7] = lod8_range;
		lod_morphing_area[7] = lod8_range-updateMorphingArea(8);
	}
	
	public int[] getLod_morphing_area() {
		return lod_morphing_area;
	}

	public Shader getShadowShader() {
		return shadowShader;
	}

	public void setShadowShader(Shader shadowShader) {
		this.shadowShader = shadowShader;
	}

	public Shader getShader() {
		return shader;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public int[] getLod_range() {
		return lod_range;
	}

	public Material getMaterial0() {
		return material0;
	}

	public void setMaterial0(Material material0) {
		this.material0 = material0;
	}

	public int getIsWaterReflected() {
		return isWaterReflected;
	}

	public void setIsWaterReflected(int isWaterReflected) {
		this.isWaterReflected = isWaterReflected;
	}

	public int getWaterReflectionShift() {
		return waterReflectionShift;
	}

	public void setWaterReflectionShift(int waterReflectionShift) {
		this.waterReflectionShift = waterReflectionShift;
	}
}
