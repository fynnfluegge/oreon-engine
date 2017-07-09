package engine.shaders.bloom;

import engine.shaders.Shader;
import engine.utils.ResourceLoader;

public class BloomShader extends Shader{
	
	private static BloomShader instance = null;
	
	public static BloomShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new BloomShader();
	    }
	      return instance;
	}
	
	protected BloomShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/Bloom/Bloom_CS.glsl"));
		
		compileShader();
	}
}
