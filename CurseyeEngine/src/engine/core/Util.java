package engine.core;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import engine.math.Matrix4f;
import engine.math.Quaternion;
import engine.math.Vec2f;
import engine.math.Vec3f;
import engine.modeling.Particle;
import engine.modeling.obj.Face;
import engine.modeling.obj.SmoothingGroup;

public class Util {

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
	
	public static FloatBuffer createFlippedBuffer(Matrix4f value)
	{
		FloatBuffer buffer = createFloatBuffer(4 * 4);
		
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				buffer.put(value.get(i, j));
		
		buffer.flip();
		
		return buffer;
	}

	
	public static String [] removeEmptyStrings(String[] data)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		for (int i = 0; i < data.length; i++)
			if(!data[i].equals(""))
				result.add(data[i]);
		
		String[] res = new String[result.size()];
		result.toArray(res);
		
		return res;
	}
	
	public static int[] toIntArray(Integer[] data)
	{
		int[] result = new int[data.length];
		
		for(int i=0; i < data.length; i++)
			result[i] = data[i].intValue();
		
		return result;
	}
	
	public static Vertex[] toVertexArray(FloatBuffer data)
	{
		Vertex[] vertices = new Vertex[data.limit() / Vertex.FLOATS];
		
		for(int i=0; i<vertices.length; i++)
		{
			vertices[i] = new Vertex();
			vertices[i].setPos(new Vec3f(data.get(),data.get(),data.get()));
			vertices[i].setTextureCoord(new Vec2f(data.get(),data.get()));
			vertices[i].setNormal(new Vec3f(data.get(),data.get(),data.get()));
		}
		
		return vertices;
	}
	
	public static Vertex[] toVertexArray(ArrayList<Vertex> data)
	{
		Vertex[] vertices = new Vertex[data.size()];
		
		for(int i=0; i<vertices.length; i++)
		{
			vertices[i] = new Vertex();
			vertices[i].setPos(data.get(i).getPos());
			vertices[i].setTextureCoord(data.get(i).getTextureCoord());
			vertices[i].setNormal(data.get(i).getNormal());
		}
		
		return vertices;
	}
	
	public static void generateNormalsCW(Vertex[] vertices, int[] indices)
	{
	    for ( int i = 0; i < indices.length; i += 3 )
	    {
	    	Vec3f v0 = vertices[indices[i    ]].getPos();
	    	Vec3f v1 = vertices[indices[i + 1]].getPos();
	    	Vec3f v2 = vertices[indices[i + 2]].getPos();
	        
	    	Vec3f normal = v1.sub(v0).cross(v2.sub(v0)).normalize();
	        
	        vertices[indices[i	  ]].setNormal(vertices[indices[i    ]].getNormal().add(normal));
	        vertices[indices[i + 1]].setNormal(vertices[indices[i + 1]].getNormal().add(normal));
	        vertices[indices[i + 2]].setNormal(vertices[indices[i + 2]].getNormal().add(normal));
	    }

	    for ( int i = 0; i < vertices.length; ++i )
	    {	
	    	vertices[i].setNormal(vertices[i].getNormal().normalize());
	    }       
	}
	
	public static void generateNormalsCCW(Vertex[] vertices, int[] indices)
	{
	    for ( int i = 0; i < indices.length; i += 3 )
	    {
	    	Vec3f v0 = vertices[indices[i    ]].getPos();
	    	Vec3f v1 = vertices[indices[i + 1]].getPos();
	    	Vec3f v2 = vertices[indices[i + 2]].getPos();
	        
	    	Vec3f normal = v2.sub(v0).cross(v1.sub(v0)).normalize();
	        
	        vertices[indices[i	  ]].setNormal(vertices[indices[i    ]].getNormal().add(normal));
	        vertices[indices[i + 1]].setNormal(vertices[indices[i + 1]].getNormal().add(normal));
	        vertices[indices[i + 2]].setNormal(vertices[indices[i + 2]].getNormal().add(normal));
	    }

	    for ( int i = 0; i < vertices.length; ++i )
	    {	
	    	vertices[i].setNormal(vertices[i].getNormal().normalize());
	    }       
	}
	
	public static void generateNormalsCW(ArrayList<Vertex> vertices, ArrayList<Integer> indices)
	{
	    for ( int i = 0; i < indices.size(); i += 3 )
	    {
	    	Vec3f v0 = vertices.get(indices.get(i)).getPos();
	    	Vec3f v1 = vertices.get(indices.get(i+1)).getPos();
	    	Vec3f v2 = vertices.get(indices.get(i+2)).getPos();
	        
	    	Vec3f normal = v1.sub(v0).cross(v2.sub(v0)).normalize();
	        
	        vertices.get(indices.get(i)).setNormal(vertices.get(indices.get(i)).getNormal().add(normal));
	        vertices.get(indices.get(i+1)).setNormal(vertices.get(indices.get(i+1)).getNormal().add(normal));
	        vertices.get(indices.get(i+2)).setNormal(vertices.get(indices.get(i+2)).getNormal().add(normal));
	    }

	    for ( int i = 0; i < vertices.size(); ++i )
	    {	
	    	vertices.get(i).setNormal(vertices.get(i).getNormal().normalize());
	    }       
	}
	
	public static void generateNormalsCCW(ArrayList<Vertex> vertices, ArrayList<Integer> indices)
	{
	    for ( int i = 0; i < indices.size(); i += 3 )
	    {
	    	Vec3f v0 = vertices.get(indices.get(i)).getPos();
	    	Vec3f v1 = vertices.get(indices.get(i+1)).getPos();
	    	Vec3f v2 = vertices.get(indices.get(i+2)).getPos();
	        
	    	Vec3f normal = v2.sub(v0).cross(v1.sub(v0)).normalize();
	        
	        vertices.get(indices.get(i)).setNormal(vertices.get(indices.get(i)).getNormal().add(normal));
	        vertices.get(indices.get(i+1)).setNormal(vertices.get(indices.get(i+1)).getNormal().add(normal));
	        vertices.get(indices.get(i+2)).setNormal(vertices.get(indices.get(i+2)).getNormal().add(normal));
	    }

	    for ( int i = 0; i < vertices.size(); ++i )
	    {	
	    	vertices.get(i).setNormal(vertices.get(i).getNormal().normalize());
	    }       
	}
	
	public static void generateNormalsCW(SmoothingGroup smoothingGroup)
	{
	    for (Face face : smoothingGroup.getFaces())
	    {
	    	Vec3f v0 = smoothingGroup.getVertices().get(face.getIndices()[0]).getPos();
	    	Vec3f v1 = smoothingGroup.getVertices().get(face.getIndices()[1]).getPos();
	    	Vec3f v2 = smoothingGroup.getVertices().get(face.getIndices()[2]).getPos();
	        
	    	Vec3f normal = v1.sub(v0).cross(v2.sub(v0)).normalize();
	        
	    	smoothingGroup.getVertices().get(face.getIndices()[0]).setNormal(
	    			smoothingGroup.getVertices().get(face.getIndices()[0]).getNormal().add(normal));
	    	smoothingGroup.getVertices().get(face.getIndices()[1]).setNormal(
	    			smoothingGroup.getVertices().get(face.getIndices()[1]).getNormal().add(normal));
	    	smoothingGroup.getVertices().get(face.getIndices()[2]).setNormal(
	    			smoothingGroup.getVertices().get(face.getIndices()[2]).getNormal().add(normal));
	    }

	    for (Vertex vertex : smoothingGroup.getVertices())
	    {	
	    	vertex.setNormal(vertex.getNormal().normalize());
	    }       
	}
	
	public static void generateNormalsCCW(SmoothingGroup smoothingGroup)
	{
		  for (Face face : smoothingGroup.getFaces())
		    {
		    	Vec3f v0 = smoothingGroup.getVertices().get(face.getIndices()[0]).getPos();
		    	Vec3f v1 = smoothingGroup.getVertices().get(face.getIndices()[1]).getPos();
		    	Vec3f v2 = smoothingGroup.getVertices().get(face.getIndices()[2]).getPos();
		        
		    	Vec3f normal = v2.sub(v0).cross(v1.sub(v0)).normalize();
		        
		    	smoothingGroup.getVertices().get(face.getIndices()[0]).setNormal(
		    			smoothingGroup.getVertices().get(face.getIndices()[0]).getNormal().add(normal));
		    	smoothingGroup.getVertices().get(face.getIndices()[1]).setNormal(
		    			smoothingGroup.getVertices().get(face.getIndices()[1]).getNormal().add(normal));
		    	smoothingGroup.getVertices().get(face.getIndices()[2]).setNormal(
		    			smoothingGroup.getVertices().get(face.getIndices()[2]).getNormal().add(normal));
		    }

		    for (Vertex vertex : smoothingGroup.getVertices())
		    {	
		    	vertex.setNormal(vertex.getNormal().normalize());
		    }     
	}
	
	public static Quaternion normalizePlane(Quaternion plane)
	{
		float mag;
		mag = (float) Math.sqrt(plane.getX() * plane.getX() + plane.getY() * plane.getY() + plane.getZ() * plane.getZ());
		plane.setX(plane.getX()/mag);
		plane.setY(plane.getY()/mag);
		plane.setZ(plane.getZ()/mag);
		plane.setW(plane.getW()/mag);
	
		return plane;
	}
	
	public static Vec2f[] texCoordsFromFontMap(char x)
	{
		float x_ = (x%16)/16.0f;
		float y_ = (x/16)/16.0f;
		Vec2f[] texCoords = new Vec2f[4];
		texCoords[0] = new Vec2f(x_, y_ + 1.0f/16.0f);
		texCoords[1] = new Vec2f(x_, y_);
		texCoords[2] = new Vec2f(x_ + 1.0f/16.0f, y_ + 1.0f/16.0f);
		texCoords[3] = new Vec2f(x_ + 1.0f/16.0f, y_);
		
		return texCoords;
	}
}
