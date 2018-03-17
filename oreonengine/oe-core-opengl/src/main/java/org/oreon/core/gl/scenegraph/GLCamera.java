package org.oreon.core.gl.scenegraph;

import java.nio.FloatBuffer;

import org.oreon.core.gl.buffers.GLUBO;
import org.oreon.core.platform.GLFWInput;
import org.oreon.core.scenegraph.Camera;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;

public class GLCamera extends Camera{

	private GLUBO ubo;
	private FloatBuffer floatBuffer;
	private final int bufferSize = Float.BYTES * (4+16+(6*4));
	
	public GLCamera(GLFWInput input) {
		
		super();
		
		setInput(input);
	}
	
	public void init(){

		ubo = new GLUBO();
		ubo.setBinding_point_index(Constants.CameraUniformBlockBinding);
		ubo.bindBufferBase();
		ubo.allocate(bufferSize);
		floatBuffer = BufferUtil.createFloatBuffer(bufferSize);
	}
	
	public void update()
	{
		super.update();
		
		updateUBO();
	}
	
	private void updateUBO(){
		
		floatBuffer.clear();
		floatBuffer.put(BufferUtil.createFlippedBuffer(getPosition()));
		floatBuffer.put(0);
		floatBuffer.put(BufferUtil.createFlippedBuffer(getViewMatrix()));
		floatBuffer.put(BufferUtil.createFlippedBuffer(getViewProjectionMatrix()));
		floatBuffer.put(BufferUtil.createFlippedBuffer(getFrustumPlanes()));
		ubo.updateData(floatBuffer, bufferSize);
	}
}
