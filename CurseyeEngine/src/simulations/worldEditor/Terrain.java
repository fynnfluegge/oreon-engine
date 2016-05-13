package simulations.worldEditor;

import modules.terrain.TerrainObject;
import engine.shaders.Shader;

public class Terrain extends TerrainObject{
	

	private String heightPath = "./res/terrains/terrain2/GTAV.jpg";
	private String normalPath = "./res/terrains/terrain2/GTAV_NRM.jpg";
	private String ambientPath = "./res/terrains/terrain2/GTAV_OCC.jpg";
	private String splatPath = "./res/terrains/terrain2/splat.jpg";
			
	private String material1_DIF  = "./res/textures/materials/grass/grass1/grass1_COLOR.jpg";
	private String material1_NRM  = "./res/textures/materials/grass/grass1/grass1_NRM.jpg";
	private String material1_DISP = "./res/textures/materials/grass/grass1/grass1_DISP.jpg";

	private String material2_DIF = "./res/textures/materials/grass/grass2/grass2.jpg";
	private String material2_NRM = "./res/textures/materials/grass/grass2/grass2_NRM.jpg";
	private String material2_DISP = "./res/textures/materials/grass/grass2/grass2_DISP.jpg";
	
	private String material3_DIF = "./res/textures/materials/rock/rock1/rock1.jpg";
	private String material3_NRM = "./res/textures/materials/rock/rock1/rock1_NRM.jpg";  
	private String material3_DISP = "./res/textures/materials/rock/rock1/rock1_DISP.jpg";
	
	public Terrain(String file, Shader grid, Shader tessellation) {
		super(file, grid, tessellation);
		// TODO Auto-generated constructor stub
	}
	
//	public Terrain() {
		
		
//		super(64);
//
//		getTransform().setLocalScaling(getScaleXZ(), getScaleY(), getScaleXZ());
//		getTransform().setLocalTranslation(-(getScaleXZ()/2),0,-(getScaleXZ()/2));	
//		
//		setHeightmap(new Texture(heightPath));
//		getHeightmap().bind();
//		getHeightmap().mipmap();
//		setNormalmap(new Texture(normalPath));
//		getNormalmap().bind();
//		getNormalmap().mipmap();
//		setAmbientmap(new Texture(ambientPath));
//		getAmbientmap().bind();
//		getAmbientmap().mipmap();
//		setSplatmap(new Texture(splatPath));
//		getSplatmap().bind();
//		getSplatmap().mipmap();
//		
//		setMaterial1(new Material());
//		getMaterial1().setEmission(0);
//		getMaterial1().setShininess(50);
//		getMaterial1().setDiffusemap(new Texture(material1_DIF));
//		getMaterial1().getDiffusemap().bind();
//		getMaterial1().getDiffusemap().mipmap();
//		getMaterial1().setNormalmap(new Texture(material1_NRM));
//		getMaterial1().getNormalmap().bind();
//		getMaterial1().getNormalmap().mipmap();
//		getMaterial1().setDisplacemap(new Texture(material1_DISP));
//		getMaterial1().getDisplacemap().bind();
//		getMaterial1().getDisplacemap().mipmap();
//		
//		setMaterial2(new Material());
//		getMaterial2().setEmission(0);
//		getMaterial2().setShininess(50);
//		getMaterial2().setDiffusemap(new Texture(material2_DIF));
//		getMaterial2().getDiffusemap().bind();
//		getMaterial2().getDiffusemap().mipmap();
//		getMaterial2().setNormalmap(new Texture(material2_NRM));
//		getMaterial2().getNormalmap().bind();
//		getMaterial2().getNormalmap().mipmap();
//		getMaterial2().setDisplacemap(new Texture(material2_DISP));
//		getMaterial2().getDisplacemap().bind();
//		getMaterial2().getDisplacemap().mipmap();
//		
//		setMaterial3(new Material());
//		getMaterial3().setEmission(0);
//		getMaterial3().setShininess(50);
//		getMaterial3().setDiffusemap(new Texture(material3_DIF));
//		getMaterial3().getDiffusemap().bind();
//		getMaterial3().getDiffusemap().mipmap();
//		getMaterial3().setNormalmap(new Texture(material3_NRM));
//		getMaterial3().getNormalmap().bind();
//		getMaterial3().getNormalmap().mipmap();
//		getMaterial3().setDisplacemap(new Texture(material3_DISP));
//		getMaterial3().getDisplacemap().bind();
//		getMaterial3().getDisplacemap().mipmap();
//		
//		setSightRangeFactor(10);
//	}

	public String getMaterial1_DIF() {
		return material1_DIF;
	}

	public void setMaterial1_DIF(String material1_DIF) {
		this.material1_DIF = material1_DIF;
	}

	public String getMaterial1_NRM() {
		return material1_NRM;
	}

	public void setMaterial1_NRM(String material1_NRM) {
		this.material1_NRM = material1_NRM;
	}

	public String getMaterial1_DISP() {
		return material1_DISP;
	}

	public void setMaterial1_DISP(String material1_DISP) {
		this.material1_DISP = material1_DISP;
	}

	public String getMaterial2_DIF() {
		return material2_DIF;
	}

	public void setMaterial2_DIF(String material2_DIF) {
		this.material2_DIF = material2_DIF;
	}

	public String getMaterial2_NRM() {
		return material2_NRM;
	}

	public void setMaterial2_NRM(String material2_NRM) {
		this.material2_NRM = material2_NRM;
	}

	public String getMaterial2_DISP() {
		return material2_DISP;
	}

	public void setMaterial2_DISP(String material2_DISP) {
		this.material2_DISP = material2_DISP;
	}

	public String getMaterial3_DIF() {
		return material3_DIF;
	}

	public void setMaterial3_DIF(String material3_DIF) {
		this.material3_DIF = material3_DIF;
	}

	public String getMaterial3_NRM() {
		return material3_NRM;
	}

	public void setMaterial3_NRM(String material3_NRM) {
		this.material3_NRM = material3_NRM;
	}

	public String getMaterial3_DISP() {
		return material3_DISP;
	}

	public void setMaterial3_DISP(String material3_DISP) {
		this.material3_DISP = material3_DISP;
	}

	public String getHeightPath() {
		return heightPath;
	}

	public void setHeightPath(String heightPath) {
		this.heightPath = heightPath;
	}

	public String getNormalPath() {
		return normalPath;
	}

	public void setNormalPath(String normalPath) {
		this.normalPath = normalPath;
	}

	public String getSplatPath() {
		return splatPath;
	}

	public void setSplatPath(String splatPath) {
		this.splatPath = splatPath;
	}

	public String getAmbientPath() {
		return ambientPath;
	}

	public void setAmbientPath(String ambientPath) {
		this.ambientPath = ambientPath;
	}

}
