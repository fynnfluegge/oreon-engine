package org.oreon.core.gl.light;

import org.oreon.core.gl.buffers.GLUBO;
import org.oreon.core.light.DirectionalLight;
import org.oreon.core.shadow.PSSMCamera;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.utils.BufferUtil;
import org.oreon.core.utils.Constants;

public class GLDirectionalLight extends DirectionalLight{

	private static GLDirectionalLight instance = null;
	
	public static GLDirectionalLight getInstance(){
		if(instance == null) 
	    {
	    	instance = new GLDirectionalLight();
	    }
	      return instance;
	}
	
	protected GLDirectionalLight(){
		
		super(); 
		
		setUbo_light(new GLUBO());
		getUbo_light().setBinding_point_index(Constants.DirectionalLightUniformBlockBinding);
		getUbo_light().bindBufferBase();
		getUbo_light().allocate(getLightBufferSize());
		setFloatBufferLight(BufferUtil.createFloatBuffer(getLightBufferSize()));
		getFloatBufferLight().put(BufferUtil.createFlippedBuffer(getDirection()));
		getFloatBufferLight().put(intensity);
		getFloatBufferLight().put(BufferUtil.createFlippedBuffer(getAmbient()));
		getFloatBufferLight().put(0);
		getFloatBufferLight().put(BufferUtil.createFlippedBuffer(getColor()));
		getFloatBufferLight().put(0);
		getUbo_light().updateData(getFloatBufferLight(), getLightBufferSize());
		
		setUbo_matrices(new GLUBO());
		getUbo_matrices().setBinding_point_index(Constants.LightMatricesUniformBlockBinding);
		getUbo_matrices().bindBufferBase();
		getUbo_matrices().allocate(getMatricesBufferSize());
	
		getUbo_matrices().updateData(getFloatBufferMatrices(), getMatricesBufferSize());
	}
	
	public void update(){
		
		if (CoreSystem.getInstance().getScenegraph().getCamera().isCameraRotated() || 
				CoreSystem.getInstance().getScenegraph().getCamera().isCameraMoved()){
			getFloatBufferMatrices().clear();
			for (PSSMCamera lightCamera : getSplitLightCameras()){
				lightCamera.update(getM_View(), getUp(), getRight());
				getFloatBufferMatrices().put(BufferUtil.createFlippedBuffer(lightCamera.getM_orthographicViewProjection()));
			}
			getUbo_matrices().updateData(getFloatBufferMatrices(), getMatricesBufferSize());
		}
	}
}
