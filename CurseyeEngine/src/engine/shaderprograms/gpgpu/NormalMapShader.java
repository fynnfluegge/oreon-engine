package engine.shaderprograms.gpgpu;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import engine.core.ResourceLoader;
import engine.core.Texture;
import engine.shaderprograms.Shader;

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
		
		addComputeShader(ResourceLoader.loadShader("gpgpu/NormalMap.glsl"));
		compileShader();
	
		addUniform("displacementmap");
		addUniform("N");
		addUniform("normalStrength");
	}
	
	public void sendUniforms(Texture heightmap, int N, float strength)
	{
		glActiveTexture(GL_TEXTURE0);
		heightmap.bind();
		setUniformi("displacementmap", 0);
		setUniformi("N", N);
		setUniformf("normalStrength", strength);
	}
}
