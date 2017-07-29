package engine.shaders.lightScattering;

import engine.components.light.DirectionalLight;
import engine.math.Matrix4f;
import engine.shaders.Shader;
import engine.utils.ResourceLoader;

public class SunLightScatteringShader extends Shader{
	
	private static SunLightScatteringShader instance = null;
	
	public static SunLightScatteringShader getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new SunLightScatteringShader();
	    }
	      return instance;
	}
	
	protected SunLightScatteringShader()
	{
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/computing/lightScattering/lightScattering_CS.glsl"));
		
		compileShader();
		
		addUniform("sunWorldPosition");
		addUniform("windowWidth");
		addUniform("windowHeight");
		addUniform("viewProjectionMatrix");
	}
	
	public void updateUniforms(int windowWidth, int windowHeight, Matrix4f viewProjectionMatrix) {
		setUniformf("windowWidth", windowWidth);
		setUniformf("windowHeight", windowHeight);
		setUniform("viewProjectionMatrix", viewProjectionMatrix);
		setUniform("sunWorldPosition", DirectionalLight.getInstance().getDirection().mul(-2800));
	}
}
