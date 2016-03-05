package engine.renderer.terrain;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import engine.core.Texture;
import engine.core.Util;
import engine.gameObject.components.Material;
import engine.renderer.terrain.fractals.FractalMaps;
import engine.shaderprograms.Shader;

public class TerrainConfiguration {

	private BufferedImage heightmapSampler;
	private float scaleY;
	private float scaleXZ;
	private int bezíer;
	private float sightRangeFactor;
	private float texDetail;
	private int tessellationFactor;
	private float tessellationSlope;
	private float tessellationShift;
	private int detailRange;
	private Texture heightmap;
	private Texture normalmap;
	private Texture ambientmap;
	private Texture splatmap;
	private Material material1;
	private Material material2;
	private Material material3;
	private ArrayList<FractalMaps> fractals = new ArrayList<FractalMaps>();
	private Shader gridShader;
	private Shader tessellationShader;
	
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
					if(tokens[0].equals("heightmap")){
						setHeightmap(new Texture(tokens[1]));
						getHeightmap().bind();
						getHeightmap().mipmap();
						BufferedImage img = null;
						try {
						    img = ImageIO.read(new File(tokens[1]));
						} catch (IOException e) {
						}
						setHeightmapSampler(img);
					}
					if(tokens[0].equals("normalmap")){
						setNormalmap(new Texture(tokens[1]));
						getNormalmap().bind();
						getNormalmap().mipmap();
					}
					if(tokens[0].equals("ambientmap")){
						setAmbientmap(new Texture(tokens[1]));
						getAmbientmap().bind();
						getAmbientmap().mipmap();
					}
					if(tokens[0].equals("splatmap")){
						setSplatmap(new Texture(tokens[1]));
						getSplatmap().bind();
						getSplatmap().mipmap();
					}
					if(tokens[0].equals("material1_DIF")){
						setMaterial1(new Material());
						getMaterial1().setDiffusemap(new Texture(tokens[1]));
						getMaterial1().getDiffusemap().bind();
						getMaterial1().getDiffusemap().mipmap();
					}
					if(tokens[0].equals("material1_NRM")){
						getMaterial1().setNormalmap(new Texture(tokens[1]));
						getMaterial1().getNormalmap().bind();
						getMaterial1().getNormalmap().mipmap();
					}
					if(tokens[0].equals("material1_DISP")){
						getMaterial1().setDisplacemap(new Texture(tokens[1]));
						getMaterial1().getDisplacemap().bind();
						getMaterial1().getDisplacemap().mipmap();
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
						getMaterial2().setDiffusemap(new Texture(tokens[1]));
						getMaterial2().getDiffusemap().bind();
						getMaterial2().getDiffusemap().mipmap();
					}
					if(tokens[0].equals("material2_NRM")){
						getMaterial2().setNormalmap(new Texture(tokens[1]));
						getMaterial2().getNormalmap().bind();
						getMaterial2().getNormalmap().mipmap();
					}
					if(tokens[0].equals("material2_DISP")){
						getMaterial2().setDisplacemap(new Texture(tokens[1]));
						getMaterial2().getDisplacemap().bind();
						getMaterial2().getDisplacemap().mipmap();
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
						getMaterial3().setDiffusemap(new Texture(tokens[1]));
						getMaterial3().getDiffusemap().bind();
						getMaterial3().getDiffusemap().mipmap();
					}
					if(tokens[0].equals("material3_NRM")){
						getMaterial3().setNormalmap(new Texture(tokens[1]));
						getMaterial3().getNormalmap().bind();
						getMaterial3().getNormalmap().mipmap();
					}
					if(tokens[0].equals("material3_DISP")){
						getMaterial3().setDisplacemap(new Texture(tokens[1]));
						getMaterial3().getDisplacemap().bind();
						getMaterial3().getDisplacemap().mipmap();
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
					if(tokens[0].equals("bezier")){
						setBezíer(Integer.valueOf(tokens[1]));
					}
					if(tokens[0].equals("detailRange")){
						setDetailRange(Integer.valueOf(tokens[1]));
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
	public Texture getHeightmap() {
		return heightmap;
	}
	public void setHeightmap(Texture heightmap) {
		this.heightmap = heightmap;
	}
	public Texture getNormalmap() {
		return normalmap;
	}
	public void setNormalmap(Texture normalmap) {
		this.normalmap = normalmap;
	}
	public Texture getAmbientmap() {
		return ambientmap;
	}
	public void setAmbientmap(Texture ambientmap) {
		this.ambientmap = ambientmap;
	}
	public Texture getSplatmap() {
		return splatmap;
	}
	public void setSplatmap(Texture splatmap) {
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
	public BufferedImage getHeightmapSampler() {
		return heightmapSampler;
	}
	public void setHeightmapSampler(BufferedImage heightmapSampler) {
		this.heightmapSampler = heightmapSampler;
	}

	public ArrayList<FractalMaps> getFractals() {
		return fractals;
	}

	public void setFractals(ArrayList<FractalMaps> fractals) {
		this.fractals = fractals;
	}

	public Shader getGridShader() {
		return gridShader;
	}

	public void setGridShader(Shader gridShader) {
		this.gridShader = gridShader;
	}

	public Shader getTessellationShader() {
		return tessellationShader;
	}

	public void setTessellationShader(Shader tessellationShader) {
		this.tessellationShader = tessellationShader;
	}
}
