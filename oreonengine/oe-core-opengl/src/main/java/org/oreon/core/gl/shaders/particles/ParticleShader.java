package org.oreon.core.gl.shaders.particles;

import static org.lwjgl.opengl.GL30.GL_INTERLEAVED_ATTRIBS;
import static org.lwjgl.opengl.GL30.glTransformFeedbackVaryings;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.util.ResourceLoader;

public class ParticleShader extends GLShader {
	
	private static ParticleShader instance = null;
	
	public static ParticleShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new ParticleShader();
	    }
	      return instance;
	}
	
	protected ParticleShader()
	{
		super();
		
		
		addVertexShader(ResourceLoader.loadShader("Vertex/TransformFeedback.glsl"));  
		addGeometryShader(ResourceLoader.loadShader("particleSystem/transformGeometry.shader"));
	    
		compileShader();
		
		addUniform("clear");
		
		CharSequence[] varyings = new String[4]; 
	    varyings[0] = "position1";
	    varyings[1] = "velocity1";
	    varyings[2] = "alive1";
	    varyings[3] = "size1";
	    
	   	
	    glTransformFeedbackVaryings(getProgram(), varyings, GL_INTERLEAVED_ATTRIBS);
	    
	    compileShader();
	}
	
	public void updateUniforms(int clear)
	{
		setUniformi("clear", clear);
	}
}
