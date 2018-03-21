package org.oreon.core.gl.context;

import org.oreon.core.context.Camera;
import org.oreon.core.gl.buffers.GLUBO;
import org.oreon.core.util.Constants;

public class GLCamera extends Camera{

	private GLUBO ubo;
	
	public GLCamera() {
		
		super();
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
