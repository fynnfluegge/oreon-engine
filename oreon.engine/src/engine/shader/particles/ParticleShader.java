package engine.shader.particles;

import engine.shader.Shader;
import engine.utils.ResourceLoader;

import static org.lwjgl.opengl.GL30.GL_INTERLEAVED_ATTRIBS;
import static org.lwjgl.opengl.GL30.glTransformFeedbackVaryings;

public class ParticleShader extends Shader {
	
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
