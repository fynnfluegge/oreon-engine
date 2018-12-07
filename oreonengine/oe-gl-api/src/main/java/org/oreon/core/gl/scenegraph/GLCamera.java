package org.oreon.core.gl.scenegraph;

import org.oreon.core.gl.memory.GLUniformBuffer;
import org.oreon.core.math.Vec3f;
import org.oreon.core.scenegraph.Camera;
import org.oreon.core.util.Constants;

public class GLCamera extends Camera{

	private GLUniformBuffer ubo;
	
	public GLCamera() {
		
		super(new Vec3f(-160,45,-72), new Vec3f(0.5668308f,-0.028192917f,0.82335174f),
				new Vec3f(0.015936304f,0.9996025f,0.023256794f));
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
