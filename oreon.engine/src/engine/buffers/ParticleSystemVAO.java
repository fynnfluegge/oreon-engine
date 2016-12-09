package engine.buffers;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.GL_RASTERIZER_DISCARD;
import static org.lwjgl.opengl.GL30.GL_TRANSFORM_FEEDBACK_BUFFER;
import static org.lwjgl.opengl.GL30.glBeginTransformFeedback;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glEndTransformFeedback;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL40.GL_TRANSFORM_FEEDBACK;
import static org.lwjgl.opengl.GL40.glBindTransformFeedback;
import static org.lwjgl.opengl.GL40.glDrawTransformFeedback;
import static org.lwjgl.opengl.GL40.glGenTransformFeedbacks;

import engine.geometry.Particle;
import engine.utils.BufferAllocation;

import static org.lwjgl.opengl.GL40.glDeleteTransformFeedbacks;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;

public class ParticleSystemVAO {

	private boolean firstDraw;
	private int size;
	private int vaoId;
	private int vbo;
	private int tfb;
	
	public ParticleSystemVAO()
	{
		firstDraw = true;
		vaoId = glGenVertexArrays();
		vbo = glGenBuffers();
		tfb = glGenTransformFeedbacks();
	}
	
	public void init(Particle[] particles)
	{
		size = particles.length;
		
		glBindVertexArray(vaoId);
		
		glBindTransformFeedback(GL_TRANSFORM_FEEDBACK, tfb);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, BufferAllocation.createFlippedBufferAOS(particles), GL_DYNAMIC_DRAW);
        glBindBufferBase(GL_TRANSFORM_FEEDBACK_BUFFER, 0, vbo);
        
        
        glVertexAttribPointer(0,3,GL_FLOAT,false,Particle.BYTES,0);
	    glVertexAttribPointer(1,3,GL_FLOAT,false,Particle.BYTES,12);
	    glVertexAttribPointer(2,1,GL_FLOAT,false,Particle.BYTES,24);
	    glVertexAttribPointer(3,1,GL_FLOAT,false,Particle.BYTES,28);
	}
	
	public void updateParticles()
	{
		glBindVertexArray(vaoId);
		
		glEnable(GL_RASTERIZER_DISCARD);
		glBindBuffer(GL_ARRAY_BUFFER, vbo); 
	    glBindTransformFeedback(GL_TRANSFORM_FEEDBACK, tfb);
	    
	    glEnableVertexAttribArray(0);
	    glEnableVertexAttribArray(1);
	    glEnableVertexAttribArray(2);
	    glEnableVertexAttribArray(3);
	    
	    glBeginTransformFeedback(GL_POINTS);
	    
	    if (firstDraw) {
	        glDrawArrays(GL_POINTS, 0, size);
	        firstDraw = false;
	    }
	    else {
	        glDrawTransformFeedback(GL_POINTS, tfb);
	    } 
	    
	    glEndTransformFeedback();
	    
	    glDisableVertexAttribArray(0);
	    glDisableVertexAttribArray(1);
	    glDisableVertexAttribArray(2);
	    glDisableVertexAttribArray(3);
	    
	    glDisable(GL_RASTERIZER_DISCARD);
	}
	
	public void draw()
	{
		glBindVertexArray(vaoId);
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(3);
		
		glDrawArrays(GL_POINTS, 0, size);
		
	    glDisableVertexAttribArray(0);
	    glDisableVertexAttribArray(3);
	    
	    glBindVertexArray(0);
	}
	
	public void shutdown()
	{
		glDeleteBuffers(vbo);
		glDeleteTransformFeedbacks(tfb);
		glDeleteVertexArrays(vaoId);
	}
}
