package modules.lighting;

import engine.math.Vec3f;

public class DirectionalLight extends Light{

	private Vec3f direction;
	
	public DirectionalLight(Vec3f direction, Vec3f ambient, Vec3f color, float intensity) {
		
		super(ambient, color, intensity);
		this.direction = direction;
	}
	
	public Vec3f getDirection() {
		return direction;
	}
	public void setDirection(Vec3f direction) {
		this.direction = direction;
	}
}
