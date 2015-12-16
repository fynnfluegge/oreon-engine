package engine.renderpipeline.data;

import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.GL_STATIC_READ;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import engine.core.Util;
import engine.math.Vec2f;


public class SSBO {
	
	private int ssbo;
	
	public SSBO()
	{
		ssbo = glGenBuffers();
	}
	
	public void addData(Vec2f[] buffer)
	{
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
		glBufferData(GL_SHADER_STORAGE_BUFFER, Util.createFlippedBuffer(buffer), GL_STATIC_READ);
	}
	
	public void addData(int[] buffer)
	{
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
		glBufferData(GL_SHADER_STORAGE_BUFFER, Util.createFlippedBuffer(buffer), GL_STATIC_READ);
	}
	
	public void bind(int index)
	{
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, index, ssbo);
	}

}
