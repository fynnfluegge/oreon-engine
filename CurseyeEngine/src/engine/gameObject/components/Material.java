package engine.gameObject.components;

import engine.core.Texture;
import engine.math.Vec3f;


public class Material extends Component{
	
	private String name;
	private Texture diffusemap;
	private Texture normalmap;
	private Texture displacemap;
	private Texture ambientmap;
	private Texture specularmap;
	private Texture alphamap;
	private Vec3f color;
	private float alpha;
	private float displaceScale;
	private float emission;
	private float shininess;
	
	public Texture getDiffusemap() {
		return diffusemap;
	}
	public void setDiffusemap(Texture diffusemap) {
		this.diffusemap = diffusemap;
	}
	public Texture getNormalmap() {
		return normalmap;
	}
	public void setNormalmap(Texture normalmap) {
		this.normalmap = normalmap;
	}
	public Texture getDisplacemap() {
		return displacemap;
	}
	public void setDisplacemap(Texture displacemap) {
		this.displacemap = displacemap;
	}
	public Texture getAmbientmap() {
		return ambientmap;
	}
	public void setAmbientmap(Texture ambientmap) {
		this.ambientmap = ambientmap;
	}
	public Texture getSpecularmap() {
		return specularmap;
	}
	public void setSpecularmap(Texture specularmap) {
		this.specularmap = specularmap;
	}
	public Texture getAlphamap() {
		return alphamap;
	}
	public void setAlphamap(Texture alphamap) {
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