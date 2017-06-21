package org.oreon.engine.engine.shaders.lightScattering;

import org.oreon.engine.engine.math.Matrix4f;
import org.oreon.engine.engine.shaders.Shader;
import org.oreon.engine.engine.utils.ResourceLoader;
import org.oreon.engine.modules.lighting.DirectionalLight;

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
