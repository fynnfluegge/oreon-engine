package modules.lighting;

import engine.math.Vec3f;
import engine.scenegraph.Node;

public abstract class Light extends Node{
	
	protected Vec3f color;
	protected float intensity;
	
	public Light(Vec3f color, float intensity)
	{
		this.color = color;
		this.intensity = intensity;
	}
	
	public Vec3f getColor() {
		return color;
	}
	public void setColor(Vec3f color) {
		this.color = color;
	}

	public float getIntensity() {
		return intensity;
	}
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
}