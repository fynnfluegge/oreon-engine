package engine.utils;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import engine.geometry.Particle;
import engine.geometry.Vertex;
import engine.math.Matrix4f;
import engine.math.Quaternion;
import engine.math.Vec2f;
import engine.math.Vec3f;

public class BufferAllocation {

	public static FloatBuffer createFloatBuffer(int size)
	{
		return BufferUtils.createFloatBuffer(size);
	}
	
	public static IntBuffer createIntBuffer(int size)
	{
		return BufferUtils.createIntBuffer(size);
	}
	
	public static DoubleBuffer createDoubleBuffer(int size)
	{
		return BufferUtils.createDoubleBuffer(size);
	}
	
	public static IntBuffer createFlippedBuffer(int... values)
	{
		IntBuffer buffer = createIntBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		
		return buffer;
	}
	
	public static FloatBuffer createFlippedBuffer(float... values)
	{
		FloatBuffer buffer = createFloatBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		
		return buffer;
	}
	
	public static DoubleBuffer createFlippedBuffer(double... values)
	{
		DoubleBuffer buffer = createDoubleBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		
		return buffer;
	}
	
	public static FloatBuffer createFlippedBufferAOS(Vertex[] vertices)
	{
		FloatBuffer buffer = createFloatBuffer(vertices.length * Vertex.FLOATS);
		
		for(int i = 0; i < vertices.length; i++)
		{
			buffer.put(vertices[i].getPos().getX());
			buffer.put(vertices[i].getPos().getY());
			buffer.put(vertices[i].getPos().getZ());
			buffer.put(vertices[i].getNormal().getX());
			buffer.put(vertices[i].getNormal().getY());
			buffer.put(vertices[i].getNormal().getZ());
			buffer.put(vertices[i].getTextureCoord().getX());
			buffer.put(vertices[i].getTextureCoord().getY());
			
			if (vertices[i].getTangent() != null && vertices[i].getBitangent() != null){
				buffer.put(vertices[i].getTangent().getX());
				buffer.put(vertices[i].getTangent().getY());
				buffer.put(vertices[i].getTangent().getZ());
				buffer.put(vertices[i].getBitangent().getX());
				buffer.put(vertices[i].getBitangent().getY());
				buffer.put(vertices[i].getBitangent().getZ());
			}
		}
		
		buffer.flip();
		
		return buffer;
	}
	
	public static FloatBuffer createFlippedBufferSOA(Vertex[] vertices)
	{
		FloatBuffer buffer = createFloatBuffer(vertices.length * Vertex.FLOATS);
		
		for(int i = 0; i < vertices.length; i++)
		{
			buffer.put(vertices[i].getPos().getX());
			buffer.put(vertices[i].getPos().getY());
			buffer.put(vertices[i].getPos().getZ());
		}
		
		for(int i = 0; i < vertices.length; i++)
		{
			buffer.put(vertices[i].getNormal().getX());
			buffer.put(vertices[i].getNormal().getY());
			buffer.put(vertices[i].getNormal().getZ());
		}
			
		for(int i = 0; i < vertices.length; i++)
		{
			buffer.put(vertices[i].getTextureCoord().getX());
			buffer.put(vertices[i].getTextureCoord().getY());
		}	
		
		buffer.flip();
		
		return buffer;
	}
	
	public static FloatBuffer createFlippedBufferAOS(Particle[] particles)
	{
		FloatBuffer buffer = createFloatBuffer(particles.length * Particle.FLOATS);
		
		for(int i = 0; i < particles.length; i++)
		{
			buffer.put(particles[i].getPosition().getX());
			buffer.put(particles[i].getPosition().getY());
			buffer.put(particles[i].getPosition().getZ());
			buffer.put(particles[i].getVelocity().getX());
			buffer.put(particles[i].getVelocity().getY());
			buffer.put(particles[i].getVelocity().getZ());
			buffer.put(particles[i].getAlive());
			buffer.put(particles[i].getSize());
		}
		
		buffer.flip();
		
		return buffer;
	}
	
	public static FloatBuffer createFlippedBuffer(Vec3f[] vector)
	{
		FloatBuffer buffer = createFloatBuffer(vector.length * Float.BYTES * 3);
		
		for (int i = 0; i < vector.length; i++)
		{
			buffer.put(vector[i].getX());
			buffer.put(vector[i].getY());
			buffer.put(vector[i].getZ());
		}
		
		buffer.flip();
		
		return buffer;
	}
	
	public static FloatBuffer createFlippedBuffer(Quaternion[] vector)
	{
		FloatBuffer buffer = createFloatBuffer(vector.length * Float.BYTES * 4);
		
		for (int i = 0; i < vector.length; i++)
		{
			buffer.put(vector[i].getX());
			buffer.put(vector[i].getY());
			buffer.put(vector[i].getZ());
			buffer.put(vector[i].getW());
		}
		
		buffer.flip();
		
		return buffer;
	}
	
	public static FloatBuffer createFlippedBuffer(Vec3f vector)
	{
		FloatBuffer buffer = createFloatBuffer(Float.BYTES * 3);
		
		buffer.put(vector.getX());
		buffer.put(vector.getY());
		buffer.put(vector.getZ());
		
		buffer.flip();
		
		return buffer;
	}
	
	public static FloatBuffer createFlippedBuffer(Quaternion vector)
	{
		FloatBuffer buffer = createFloatBuffer(Float.BYTES * 4);
		
		buffer.put(vector.getX());
		buffer.put(vector.getY());
		buffer.put(vector.getZ());
		buffer.put(vector.getW());
		
		buffer.flip();
		
		return buffer;
	}
	
	public static FloatBuffer createFlippedBuffer(Vec2f[] vector)
	{
		FloatBuffer buffer = createFloatBuffer(vector.length * Float.BYTES * 2);
		
		for (int i = 0; i < vector.length; i++)
		{
			buffer.put(vector[i].getX());
			buffer.put(vector[i].getY());	
		}
		
		buffer.flip();
		
		return buffer;
	}
	
	public static FloatBuffer createFlippedBuffer(Matrix4f matrix)
	{
		FloatBuffer buffer = createFloatBuffer(4 * 4);
		
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				buffer.put(matrix.get(i, j));
		
		buffer.flip();
		
		return buffer;
	}
	
	public static FloatBuffer createFlippedBuffer(Matrix4f[] matrices)
	{
		FloatBuffer buffer = createFloatBuffer(4 * 4 * matrices.length);
		
		for (Matrix4f matrix : matrices){
			for (int i = 0; i < 4; i++)
				for (int j = 0; j < 4; j++)
					buffer.put(matrix.get(i, j));
		}
		
		buffer.flip();
		
		return buffer;
	}

}
