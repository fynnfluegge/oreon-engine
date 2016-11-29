package engine.geometry;

import engine.math.Vec3f;

public class Particle {
	
	public static final int BYTES = 32;
	public static final int FLOATS = 8;

	private Vec3f position;
	private Vec3f velocity;
	private float alive;
	private float size;
	
	
	public Particle()
	{
		this.position = new Vec3f(0,0,0);
		this.velocity = new Vec3f(0,0,0);
		this.alive = 0;
		this.size = 0;
	}
	
	public Particle(Vec3f position, Vec3f velocity, float alive, Vec3f startPos, float size)
	{
		this.position = position;
		this.velocity = velocity;
		this.alive = alive;
		this.size = size;
	}
	public Vec3f getVelocity() {
		return velocity;
	}
	public void setVelocity(Vec3f velocity) {
		this.velocity = velocity;
	}
	public float getAlive() {
		return alive;
	}
	public void setAlive(float alive) {
		this.alive = alive;
	}
	public Vec3f getPosition() {
		return position;
	}
	public void setPosition(Vec3f position) {
		this.position = position;
	}


	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}
}
