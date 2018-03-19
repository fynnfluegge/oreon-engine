package org.oreon.core.gl.scenegraph;

import org.oreon.core.gl.buffers.GLUBO;
import org.oreon.core.platform.GLFWInput;
import org.oreon.core.scenegraph.Camera;
import org.oreon.core.util.Constants;

public class GLCamera extends Camera{

	private GLUBO ubo;
	
	
	public GLCamera(GLFWInput input) {
		
		super();
		
		setInput(input);
	}
	
	@Override
	public void init(){

		ubo = new GLUBO();
		ubo.setBinding_point_index(Constants.CameraUniformBlockBinding);
		ubo.bindBufferBase();
		ubo.allocate(bufferSize);
	}
	
	@Override
	public void update()
	{
		super.update();
		
		ubo.updateData(floatBuffer, bufferSize);
	}

}
