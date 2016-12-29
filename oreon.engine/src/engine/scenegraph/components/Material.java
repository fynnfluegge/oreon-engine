package engine.scenegraph.components;

import engine.math.Vec3f;
import engine.textures.Texture2D;


public class Material extends Component{
	
	private String name;
	private Texture2D diffusemap;
	private Texture2D normalmap;
	private Texture2D displacemap;
	private Texture2D ambientmap;
	private Texture2D specularmap;
	private Texture2D alphamap;
	private Vec3f color;
	private float alpha;
	private float displaceScale;
	private float emission;
	private float shininess;
	
	public Texture2D getDiffusemap() {
		return diffusemap;
	}
	public void setDiffusemap(Texture2D diffusemap) {
		this.diffusemap = diffusemap;
	}
	public Texture2D getNormalmap() {
		return normalmap;
	}
	public void setNormalmap(Texture2D normalmap) {
		this.normalmap = normalmap;
	}
	public Texture2D getDisplacemap() {
		return displacemap;
	}
	public void setDisplacemap(Texture2D displacemap) {
		this.displacemap = displacemap;
	}
	public Texture2D getAmbientmap() {
		return ambientmap;
	}
	public void setAmbientmap(Texture2D ambientmap) {
		this.ambientmap = ambientmap;
	}
	public Texture2D getSpecularmap() {
		return specularmap;
	}
	public void setSpecularmap(Texture2D specularmap) {
		this.specularmap = specularmap;
	}
	public Texture2D getAlphamap() {
		return alphamap;
	}
	public void setAlphamap(Texture2D alphamap) {
		this.alphamap = alphamap;
	}
	public Vec3f getColor() {
		return color;
	}
	public void setColor(Vec3f color) {
		this.color = color;
	}
	public float getAlpha() {
		return alpha;
	}
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	public float getDisplaceScale() {
		return displaceScale;
	}
	public void setDisplaceScale(float displaceScale) {
		this.displaceScale = displaceScale;
	}
	public float getEmission() {
		return emission;
	}
	public void setEmission(float emission) {
		this.emission = emission;
	}
	public float getShininess() {
		return shininess;
	}
	public void setShininess(float shininess) {
		this.shininess = shininess;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}