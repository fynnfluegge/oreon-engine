package org.oreon.core.gl.shaders.lightScattering;

import org.oreon.core.gl.light.GLDirectionalLight;
import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.util.ResourceLoader;

public class SunLightScatteringShader extends GLShader{
	
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
		setUniform("sunWorldPosition", GLDirectionalLight.getInstance().getDirection().mul(-2800));
	}
}
