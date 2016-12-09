package engine.buffers;

import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.GL_STATIC_READ;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import engine.math.Vec2f;
import engine.utils.BufferAllocation;


/**
 * Shader Storage Buffer Object
 * 
 * @author Fynn
 *
 */

public class SSBO {
	
	private int ssbo;
	
	public SSBO()
	{
		ssbo = glGenBuffers();
	}
	
	public void addData(Vec2f[] data)
	{
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
		glBufferData(GL_SHADER_STORAGE_BUFFER, BufferAllocation.createFlippedBuffer(data), GL_STATIC_READ);
	}
	
	public void addData(int[] data)
	{
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
		glBufferData(GL_SHADER_STORAGE_BUFFER, BufferAllocation.createFlippedBuffer(data), GL_STATIC_READ);
	}
	
	public void bindBufferBase(int index)
	{
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, index, ssbo);
	}

}
