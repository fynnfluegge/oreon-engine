package org.oreon.core.gl.scene;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;

import java.nio.FloatBuffer;

import org.oreon.core.buffers.UBO;
import org.oreon.core.math.Matrix4f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.scene.Camera;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.utils.BufferUtil;
import org.oreon.core.utils.Constants;

public class GLCamera extends Camera{

	private UBO ubo;
	private FloatBuffer floatBuffer;
	private final int bufferSize = Float.BYTES * (4+16+(6*4));
	
	public GLCamera() {
		
		super();
	}
	
	public void init(){

		ubo = new UBO();
		ubo.setBinding_point_index(Constants.CameraUniformBlockBinding);
		ubo.bindBufferBase();
		ubo.allocate(bufferSize);
		floatBuffer = BufferUtil.createFloatBuffer(bufferSize);
	}
	
	public void update()
	{
		setPreviousPosition(new Vec3f(getPosition()));
		setPreviousForward(new Vec3f(getForward()));
		setCameraMoved(false);
		setCameraRotated(false);
		
		setMovAmt(getMovAmt() + (0.04f * CoreSystem.getInstance().getInput().getScrollOffset()));
		setMovAmt(Math.max(0.02f, getMovAmt()));
		
		if(CoreSystem.getInstance().getInput().isKeyHolding(GLFW_KEY_W))
			move(getForward(), getMovAmt());
		if(CoreSystem.getInstance().getInput().isKeyHolding(GLFW_KEY_S))
			move(getForward(), -getMovAmt());
		if(CoreSystem.getInstance().getInput().isKeyHolding(GLFW_KEY_A))
			move(getLeft(), getMovAmt());
		if(CoreSystem.getInstance().getInput().isKeyHolding(GLFW_KEY_D))
			move(getRight(), getMovAmt());
				
		if(CoreSystem.getInstance().getInput().isKeyHolding(GLFW_KEY_UP))
			rotateX(-getRotAmt()/8f);
		if(CoreSystem.getInstance().getInput().isKeyHolding(GLFW_KEY_DOWN))
			rotateX(getRotAmt()/8f);
		if(CoreSystem.getInstance().getInput().isKeyHolding(GLFW_KEY_LEFT))
			rotateY(-getRotAmt()/8f);
		if(CoreSystem.getInstance().getInput().isKeyHolding(GLFW_KEY_RIGHT))
			rotateY(getRotAmt()/8f);
		
		// free mouse rotation
		if(CoreSystem.getInstance().getInput().isButtonHolding(2))
		{
			float dy = CoreSystem.getInstance().getInput().getLockedCursorPosition().getY() - CoreSystem.getInstance().getInput().getCursorPosition().getY();
			float dx = CoreSystem.getInstance().getInput().getLockedCursorPosition().getX() - CoreSystem.getInstance().getInput().getCursorPosition().getX();
			
			// y-axxis rotation
			
			if (dy != 0){
				setRotYamt(getRotYamt() - dy);
				setRotYstride(Math.abs(getRotYamt() * 0.1f));
			}
			
			if (getRotYamt() != 0 || getRotYstride() != 0){
				
				// up-rotation
				if (getRotYamt() < 0){
					setUpRotation(true);
					setDownRotation(false);
					rotateX(-getRotYstride() * getMouseSensitivity());
					setRotYamt(getRotYamt() + getRotYstride());
					if (getRotYamt() > 0)
						setRotYamt(0);
				}
				// down-rotation
				if (getRotYamt() > 0){
					setUpRotation(false);
					setDownRotation(true);
					rotateX(getRotYstride() * getMouseSensitivity());
					setRotYamt(getRotYamt() - getRotYstride());
					if (getRotYamt() < 0)
						setRotYamt(0);
				}
				// smooth-stop
				if (getRotYamt() == 0){
					setRotYstride(getRotYstride() * 0.95f);
					if (isUpRotation())
						rotateX(-getRotYstride() * getMouseSensitivity());
					if (isDownRotation())
						rotateX(getRotYstride() * getMouseSensitivity());
					if (getRotYstride() < 0.001f)
						setRotYstride(0);
				}
			}
			
			// x-axxis rotation
			if (dx != 0){
				setRotXamt(getRotXamt() + dx);
				setRotXstride(Math.abs(getRotXamt() * 0.1f));
			}
			
			if (getRotXamt() != 0 || getRotXstride() != 0){
				
				// right-rotation
				if (getRotXamt() < 0){
					setRightRotation(true);
					setLeftRotation(false);
					rotateY(getRotXstride() * getMouseSensitivity());
					setRotXamt(getRotXamt() + getRotXstride());
					if (getRotXamt() > 0)
						setRotXamt(0);
				}
				// left-rotation
				if (getRotXamt() > 0){
					setRightRotation(false);
					setLeftRotation(true);
					rotateY(-getRotXstride() * getMouseSensitivity());
					setRotXamt(getRotXamt() - getRotXstride());
					if (getRotXamt() < 0)
						setRotXamt(0);
				}
				// smooth-stop
				if (getRotXamt() == 0){
					setRotXstride(getRotXstride() * 0.95f);
					if (isRightRotation())
						rotateY(getRotXstride() * getMouseSensitivity());
					if (isLeftRotation())
						rotateY(-getRotXstride() * getMouseSensitivity());
					if (getRotXstride() < 0.001f)
						setRotXstride(0);
				}
			}
			
			glfwSetCursorPos(CoreSystem.getInstance().getWindow().getId(),
					CoreSystem.getInstance().getInput().getLockedCursorPosition().getX(),
					CoreSystem.getInstance().getInput().getLockedCursorPosition().getY());
		}
		
		if (!getPosition().equals(getPreviousPosition())){
			setCameraMoved(true);
		}
		
		if (!getForward().equals(getPreviousForward())){
			setCameraRotated(true);
		}
		
		setPreviousViewMatrix(getViewMatrix());
		setPreviousViewProjectionMatrix(getViewProjectionMatrix());
		setViewMatrix(new Matrix4f().View(this.getForward(), this.getUp()).mul(
				new Matrix4f().Translation(this.getPosition().mul(-1))));
		setViewProjectionMatrix(getProjectionMatrix().mul(getViewMatrix()));
		
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
