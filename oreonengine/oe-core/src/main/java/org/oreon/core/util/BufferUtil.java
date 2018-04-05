package org.oreon.core.util;

import static org.lwjgl.system.MemoryUtil.memAlloc;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Quaternion;
import org.oreon.core.math.Vec2f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.model.Vertex;
import org.oreon.core.model.Vertex.VertexAlignment;

public class BufferUtil {

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
			buffer.put(vertices[i].getPosition().getX());
			buffer.put(vertices[i].getPosition().getY());
			buffer.put(vertices[i].getPosition().getZ());
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
			buffer.put(vertices[i].getPosition().getX());
			buffer.put(vertices[i].getPosition().getY());
			buffer.put(vertices[i].getPosition().getZ());
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
	
	public static ByteBuffer createByteBuffer(Matrix4f matrix){
		
		ByteBuffer byteBuffer = memAlloc(Float.BYTES * 16);
		FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
		floatBuffer.put(BufferUtil.createFlippedBuffer(matrix));
		
		return byteBuffer;
	}
	
	public static ByteBuffer createByteBuffer(Vertex[] vertices, VertexAlignment alignment){
		
		ByteBuffer byteBuffer = allocateVertexByteBuffer(alignment, vertices.length);
		FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
		putVertices(alignment, floatBuffer, vertices);
		
		return byteBuffer;
	}
	
	public static ByteBuffer allocateVertexByteBuffer(VertexAlignment alignment, int vertexCount){
		
		ByteBuffer byteBuffer;
		
		switch(alignment){
			case POS: byteBuffer = memAlloc(Float.BYTES * 3 * vertexCount);
			case POS_UV: byteBuffer = memAlloc(Float.BYTES * 5 * vertexCount);
			case POS_NORMAL: byteBuffer = memAlloc(Float.BYTES * 6 * vertexCount);
			case POS_NORMAL_UV: byteBuffer = memAlloc(Float.BYTES * 8 * vertexCount);
			case POS_NORMAL_UV_TAN_BITAN: byteBuffer = memAlloc(Float.BYTES * 14 * vertexCount);
			default: byteBuffer = memAlloc(0);
		}
		
		return byteBuffer;
	}
	
	public static void putVertices(VertexAlignment alignment, FloatBuffer floatBuffer, Vertex[] vertices){
		
		for(int i = 0; i < vertices.length; i++)
		{
			
			floatBuffer.put(vertices[i].getPosition().getX());
			floatBuffer.put(vertices[i].getPosition().getY());
			floatBuffer.put(vertices[i].getPosition().getZ());
			
			if (alignment == VertexAlignment.POS_NORMAL ||
				alignment == VertexAlignment.POS_NORMAL_UV ||
				alignment == VertexAlignment.POS_NORMAL_UV_TAN_BITAN){
				
				floatBuffer.put(vertices[i].getNormal().getX());
				floatBuffer.put(vertices[i].getNormal().getY());
				floatBuffer.put(vertices[i].getNormal().getZ());
			}
			
			if (alignment == VertexAlignment.POS_NORMAL_UV ||
				alignment == VertexAlignment.POS_UV ||
				alignment == VertexAlignment.POS_NORMAL_UV_TAN_BITAN){
				
				floatBuffer.put(vertices[i].getTextureCoord().getX());
				floatBuffer.put(vertices[i].getTextureCoord().getY());
			}
			
			if (alignment == VertexAlignment.POS_NORMAL_UV_TAN_BITAN){
				
				floatBuffer.put(vertices[i].getTangent().getX());
				floatBuffer.put(vertices[i].getTangent().getY());
				floatBuffer.put(vertices[i].getTangent().getZ());
				floatBuffer.put(vertices[i].getBitangent().getX());
				floatBuffer.put(vertices[i].getBitangent().getY());
				floatBuffer.put(vertices[i].getBitangent().getZ());
			}
		}
	}

}
