package engine.geometry;

import engine.math.Vec2f;
import engine.math.Vec3f;

public class Geometrics {
	
	public static Mesh Quad2D()
	{
		Vertex[] vertices = new Vertex[4];
		int[] indices = {0,2,1,1,2,3};
		vertices[0] = new Vertex(new Vec3f(0,0,0), new Vec2f(0,0));
		vertices[1] = new Vertex(new Vec3f(1,0,0), new Vec2f(1,0));
		vertices[2] = new Vertex(new Vec3f(0,1,0), new Vec2f(0,1));
		vertices[3] = new Vertex(new Vec3f(1,1,0), new Vec2f(1,1));
		Mesh quad = new Mesh(vertices, indices);
		return quad;
	}
	
	public static Mesh Quad2Drot180()
	{
		Vertex[] vertices = new Vertex[4];
		int[] indices = {0,2,1,1,2,3};
		vertices[0] = new Vertex(new Vec3f(0,0,0), new Vec2f(0,1));
		vertices[1] = new Vertex(new Vec3f(1,0,0), new Vec2f(1,1));
		vertices[2] = new Vertex(new Vec3f(0,1,0), new Vec2f(0,0));
		vertices[3] = new Vertex(new Vec3f(1,1,0), new Vec2f(1,0));
		Mesh quad = new Mesh(vertices, indices);
		return quad;
	}
	
	
	public static Mesh Cube()
	{
		Vertex[] vertices = new Vertex[24];
		int[] indices = {0,1,2,0,2,3,4,5,6,4,6,7,8,9,10,8,10,11,12,13,14,12,14,15,16,17,18,16,18,19,20,21,22,20,22,23};
		
		vertices[0] = new Vertex(new Vec3f(-1,-1,-1), new Vec2f(0,1));
		vertices[1] = new Vertex(new Vec3f(-1, 1,-1), new Vec2f(0,0));
		vertices[2] = new Vertex(new Vec3f( 1, 1,-1), new Vec2f(1,0));
		vertices[3] = new Vertex(new Vec3f( 1,-1,-1), new Vec2f(1,1));
		
		vertices[4] = new Vertex(new Vec3f(-1,-1,1), new Vec2f(0,1));
		vertices[5] = new Vertex(new Vec3f(-1, 1,1), new Vec2f(0,0));
		vertices[6] = new Vertex(new Vec3f(-1, 1,-1),new Vec2f(1,0));
		vertices[7] = new Vertex(new Vec3f(-1,-1,-1), new Vec2f(1,1));
		
		vertices[8] = new Vertex(new Vec3f( 1,-1,1), new Vec2f(0,1));
		vertices[9] = new Vertex(new Vec3f( 1, 1,1), new Vec2f(0,0));
		vertices[10] = new Vertex(new Vec3f(-1,1,1), new Vec2f(1,0));
		vertices[11] = new Vertex(new Vec3f(-1,-1,1),new Vec2f(1,1));
	
		vertices[12] = new Vertex(new Vec3f(1,-1,-1), new Vec2f(0,1));
		vertices[13] = new Vertex(new Vec3f(1,1, -1), new Vec2f(0,0));
		vertices[14] = new Vertex(new Vec3f(1,1,1),   new Vec2f(1,0));
		vertices[15] = new Vertex(new Vec3f(1,-1, 1), new Vec2f(1,1));	
		
		vertices[16] = new Vertex(new Vec3f(-1,1,-1), new Vec2f(0,1));
		vertices[17] = new Vertex(new Vec3f(-1,1, 1), new Vec2f(0,0));
		vertices[18] = new Vertex(new Vec3f( 1,1, 1), new Vec2f(1,0));
		vertices[19] = new Vertex(new Vec3f( 1,1,-1), new Vec2f(1,1));
		
		vertices[20] = new Vertex(new Vec3f( 1,-1,-1), new Vec2f(0,1));
		vertices[21] = new Vertex(new Vec3f( 1,-1, 1), new Vec2f(0,0));
		vertices[22] = new Vertex(new Vec3f(-1,-1, 1), new Vec2f(1,0));
		vertices[23] = new Vertex(new Vec3f(-1,-1,-1), new Vec2f(1,1));
		
		return new Mesh(vertices, indices);
	}
}
