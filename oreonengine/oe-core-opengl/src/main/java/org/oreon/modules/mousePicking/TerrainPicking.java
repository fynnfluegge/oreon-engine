package org.oreon.modules.mousePicking;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.oreon.core.math.Quaternion;
import org.oreon.core.math.Vec2f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.utils.BufferUtil;

public class TerrainPicking {
	
	private FloatBuffer depthmapBuffer;
	private static TerrainPicking instance;
	private boolean isActive = true;

	public static TerrainPicking getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new TerrainPicking();
	    }
	      return instance;
	}
	
	private TerrainPicking(){
		depthmapBuffer = BufferUtil.createFloatBuffer(CoreSystem.getInstance().getWindow().getWidth() * 
															CoreSystem.getInstance().getWindow().getHeight());	
	}
	
	public void getTerrainPosition(){
		
		if (isActive() && glfwGetMouseButton(CoreSystem.getInstance().getWindow().getId(),1) == GLFW_PRESS){
			Vec3f pos = new Vec3f(0,0,0);
			DoubleBuffer xPos = BufferUtils.createDoubleBuffer(1);
			DoubleBuffer yPos = BufferUtils.createDoubleBuffer(1);
			glfwGetCursorPos(CoreSystem.getInstance().getWindow().getId(), xPos, yPos);
			Vec2f screenPos = new Vec2f((float) xPos.get(),(float) yPos.get());
			
			CoreSystem.getInstance().getRenderingEngine().getSceneDepthmap().bind();
			glGetTexImage(GL_TEXTURE_2D,0,GL_DEPTH_COMPONENT,GL_FLOAT,depthmapBuffer);
			float depth = depthmapBuffer.get((int) (CoreSystem.getInstance().getWindow().getWidth() * screenPos.getY() + screenPos.getX()));
			
			// window coords
			Vec2f w = new Vec2f(screenPos.getX()/CoreSystem.getInstance().getWindow().getWidth(),
								screenPos.getY()/CoreSystem.getInstance().getWindow().getHeight());
			//ndc coords
			Vec3f ndc = new Vec3f(w.getX() * 2 - 1, w.getY() * 2 - 1, depth);
			float cw = CoreSystem.getInstance().getScenegraph().getCamera().getProjectionMatrix().get(3,2) / (ndc.getZ() - CoreSystem.getInstance().getScenegraph().getCamera().getProjectionMatrix().get(2,2)); 
			Vec3f clip = ndc.mul(cw);
			Quaternion clipPos = new Quaternion(clip.getX(),clip.getY(),clip.getZ(),cw);
			Quaternion worldPos =  CoreSystem.getInstance().getScenegraph().getCamera().getViewProjectionMatrix().invert().mul(clipPos);
			worldPos = worldPos.div(worldPos.getW());
		
			pos.setX(worldPos.getX());
			pos.setY(worldPos.getY());
			pos.setZ(worldPos.getZ());
			
			System.out.println("TerrainPicking: " + pos);
		}
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}
