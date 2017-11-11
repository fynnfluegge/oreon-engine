package org.oreon.modules.gl.postprocessfilter.ssao;

import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.util.ResourceLoader;

public class SSAOShader extends GLShader{

	private static SSAOShader instance = null;
	
	public static SSAOShader getInstance()
	{
		if (instance == null){
			
			instance = new SSAOShader();
		}
		return instance;
	}
	
	protected SSAOShader(){
		
		super();
		
		addComputeShader(ResourceLoader.loadShader("shaders/post_processing/ssao/SSAO_CS.glsl"));
		compileShader();
		
		addUniform("m_View");
		addUniform("m_Proj");
		addUniform("kernelSize");
		addUniform("uRadius");
		
		for (int i=0; i<64; i++){
			addUniform("kernel[" + i + "]");
		}
	}

	public void updateUniforms(Matrix4f viewMatrix, Matrix4f projectionMatrix, Vec3f[] kernel){
		
		setUniform("m_View", viewMatrix);
		setUniform("m_Proj", projectionMatrix);
		setUniformi("kernelSize", 64);
		setUniformf("uRadius", 4f);
		
		for (int i=0; i<64; i++){
			setUniform("kernel[" + i + "]", kernel[i]);
		}
	}
}
