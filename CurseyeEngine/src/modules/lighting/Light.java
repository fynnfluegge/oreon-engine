package modules.lighting;

import engine.core.Transform;
import engine.gameObject.components.Component;
import engine.math.Vec3f;

public abstract class Light extends Component{
	
	private Vec3f ambient;
	private Vec3f color;
	private float intensity;
	private Transform transform = new Transform();
	
	public Light(Vec3f ambient, Vec3f color, float intensity)
	{
		this.ambient = ambient;
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

	public Transform getLocalTransform() {
		return transform;
	}

	public void setLocalTransform(Transform transform) {
		this.transform = transform;
	}

	public Vec3f getAmbient() {
		return ambient;
	}

	public void setAmbient(Vec3f ambient) {
		this.ambient = ambient;
	}
}
