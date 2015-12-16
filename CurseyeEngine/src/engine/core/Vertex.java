package engine.core;

import engine.math.Vec2f;
import engine.math.Vec3f;

public class Vertex {

	public static final int BYTES = 32;
	public static final int FLOATS = 8;
	
	private Vec3f pos;
	private Vec3f normal;
	private Vec2f textureCoord;
	
	public Vertex(){	
	}
	
	public Vertex(Vec3f pos)
	{
		this.setPos(pos);
		this.setTextureCoord(new Vec2f(0,0));
		this.setNormal(new Vec3f(0,0,0));
	}
	
	public Vertex(Vec3f pos, Vec2f texture)
	{
		this.setPos(pos);
		this.setTextureCoord(texture);
		this.setNormal(new Vec3f(0,0,0));
	}

	public Vec3f getPos() {
		return pos;
	}

	public void setPos(Vec3f pos) {
		this.pos = pos;
	}

	public Vec2f getTextureCoord() {
		return textureCoord;
	}

	public void setTextureCoord(Vec2f texture) {
		this.textureCoord = texture;
	}


	public Vec3f getNormal() {
		return normal;
	}

	public void setNormal(Vec3f normal) {
		this.normal = normal;
	}
}
