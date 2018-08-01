package org.oreon.core.gl.platform;

import org.oreon.core.gl.memory.GLUniformBuffer;
import org.oreon.core.math.Vec3f;
import org.oreon.core.platform.Camera;
import org.oreon.core.util.Constants;

public class GLCamera extends Camera{

	private GLUniformBuffer ubo;
	
	public GLCamera() {
		
		super(new Vec3f(0,100,0), new Vec3f(0,0,1), new Vec3f(0,1,0));
	}
	
	@Override
	public void init(){

		ubo = new GLUniformBuffer();
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

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		// destroy ubo
	}

}
