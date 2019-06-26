package org.oreon.core.gl.light;

import org.lwjgl.glfw.GLFW;
import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.memory.GLUniformBuffer;
import org.oreon.core.light.DirectionalLight;
import org.oreon.core.math.Vec3f;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Constants;

import lombok.Getter;

@Getter
public class GLDirectionalLight extends DirectionalLight{
	
	private GLUniformBuffer ubo_light;
	private GLUniformBuffer ubo_matrices;
	
	public GLDirectionalLight(){
		
		super(); 
		
		ubo_light = new GLUniformBuffer();
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
		
		ubo_matrices = new GLUniformBuffer();
		getUbo_matrices().setBinding_point_index(Constants.LightMatricesUniformBlockBinding);
		getUbo_matrices().bindBufferBase();
		getUbo_matrices().allocate(getMatricesBufferSize());
	
		getUbo_matrices().updateData(getFloatBufferMatrices(), getMatricesBufferSize());
	}
	
	public void update(){
		
		super.update();
		
		if (BaseContext.getCamera().isCameraRotated() || 
				BaseContext.getCamera().isCameraMoved()){
			
			getUbo_matrices().updateData(getFloatBufferMatrices(), getMatricesBufferSize());
		}
	
		// change sun orientation
		if (BaseContext.getInput().isKeyHolding(GLFW.GLFW_KEY_I)) {
			
			if (getDirection().getY() >= -0.8f) {
				setDirection(getDirection().add(new Vec3f(0,-0.0005f,0)).normalize());
				getFloatBufferLight().clear();
				getFloatBufferLight().flip();
				setFloatBufferLight(BufferUtil.createFloatBuffer(getLightBufferSize()));
				getFloatBufferLight().put(BufferUtil.createFlippedBuffer(getDirection()));
				getFloatBufferLight().put(intensity);
				getFloatBufferLight().put(BufferUtil.createFlippedBuffer(getAmbient()));
				getFloatBufferLight().put(0);
				getFloatBufferLight().put(BufferUtil.createFlippedBuffer(getColor()));
				getFloatBufferLight().put(0);
				getUbo_light().updateData(getFloatBufferLight(), getLightBufferSize());
				
				updateShadowMatrices();
				getUbo_matrices().updateData(getFloatBufferMatrices(), getMatricesBufferSize());
			}
		}
		if (BaseContext.getInput().isKeyHolding(GLFW.GLFW_KEY_K)) {
			
			if (getDirection().getY() <= -0.02f) {
				setDirection(getDirection().add(new Vec3f(0,0.0005f,0)).normalize());
				setFloatBufferLight(BufferUtil.createFloatBuffer(getLightBufferSize()));
				getFloatBufferLight().put(BufferUtil.createFlippedBuffer(getDirection()));
				getFloatBufferLight().put(intensity);
				getFloatBufferLight().put(BufferUtil.createFlippedBuffer(getAmbient()));
				getFloatBufferLight().put(0);
				getFloatBufferLight().put(BufferUtil.createFlippedBuffer(getColor()));
				getFloatBufferLight().put(0);
				getUbo_light().updateData(getFloatBufferLight(), getLightBufferSize());
				
				updateShadowMatrices();
				getUbo_matrices().updateData(getFloatBufferMatrices(), getMatricesBufferSize());
			}
		}
		if (BaseContext.getInput().isKeyHolding(GLFW.GLFW_KEY_J)) {
			
			setDirection(getDirection().add(new Vec3f(0.00025f,0,-0.00025f)).normalize());
			setFloatBufferLight(BufferUtil.createFloatBuffer(getLightBufferSize()));
			getFloatBufferLight().put(BufferUtil.createFlippedBuffer(getDirection()));
			getFloatBufferLight().put(intensity);
			getFloatBufferLight().put(BufferUtil.createFlippedBuffer(getAmbient()));
			getFloatBufferLight().put(0);
			getFloatBufferLight().put(BufferUtil.createFlippedBuffer(getColor()));
			getFloatBufferLight().put(0);
			getUbo_light().updateData(getFloatBufferLight(), getLightBufferSize());
			
			updateShadowMatrices();
			getUbo_matrices().updateData(getFloatBufferMatrices(), getMatricesBufferSize());
		}
		if (BaseContext.getInput().isKeyHolding(GLFW.GLFW_KEY_L)) {
			
			setDirection(getDirection().add(new Vec3f(-0.00025f,0,0.00025f)).normalize());
			setFloatBufferLight(BufferUtil.createFloatBuffer(getLightBufferSize()));
			getFloatBufferLight().put(BufferUtil.createFlippedBuffer(getDirection()));
			getFloatBufferLight().put(intensity);
			getFloatBufferLight().put(BufferUtil.createFlippedBuffer(getAmbient()));
			getFloatBufferLight().put(0);
			getFloatBufferLight().put(BufferUtil.createFlippedBuffer(getColor()));
			getFloatBufferLight().put(0);
			getUbo_light().updateData(getFloatBufferLight(), getLightBufferSize());
			
			updateShadowMatrices();
			getUbo_matrices().updateData(getFloatBufferMatrices(), getMatricesBufferSize());
		}
	}
}
