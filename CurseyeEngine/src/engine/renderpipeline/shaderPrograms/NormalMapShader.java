package engine.renderpipeline.shaderPrograms;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import engine.core.ResourceLoader;
import engine.core.Texture;
import engine.renderpipeline.Shader;

public class NormalMapShader extends Shader{

private static NormalMapShader instance = null;

	public static NormalMapShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new NormalMapShader();
	    }
	      return instance;
	}
	
	protected NormalMapShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("compute/NormalMap.glsl"));
		compileShader();
	
		addUniform("displacementmap");
		addUniform("N");
		addUniform("normalStrength");
	}
	
	public void sendUniforms(Texture displacementmap, int N, float strength)
	{
		glActiveTexture(GL_TEXTURE0);
		displacementmap.bind();
		setUniformi("displacementmap", 0);
		setUniformi("N", N);
		setUniformf("normalStrength", strength);
	}
}
