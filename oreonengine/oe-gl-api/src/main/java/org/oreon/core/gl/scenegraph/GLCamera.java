package org.oreon.core.gl.scenegraph;

import org.oreon.core.gl.memory.GLUniformBuffer;
import org.oreon.core.math.Vec3f;
import org.oreon.core.scenegraph.Camera;
import org.oreon.core.util.Constants;

public class GLCamera extends Camera{

	private GLUniformBuffer ubo;
	
	public GLCamera() {
		
		super(new Vec3f(282.08438f,450.02676f,-2860.124f), new Vec3f(0.4447886f,0.2445276f,0.86160856f),
				new Vec3f(-0.11216718f,0.96964234f,-0.21728384f));
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
