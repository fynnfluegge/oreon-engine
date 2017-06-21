package org.oreon.engine.modules.mousePicking;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import java.nio.FloatBuffer;

import org.oreon.engine.engine.core.Camera;
import org.oreon.engine.engine.core.Input;
import org.oreon.engine.engine.core.Window;
import org.oreon.engine.engine.math.Quaternion;
import org.oreon.engine.engine.math.Vec2f;
import org.oreon.engine.engine.math.Vec3f;
import org.oreon.engine.engine.utils.BufferUtil;

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
		depthmapBuffer = BufferUtil.createFloatBuffer(Window.getInstance().getWidth() * 
															Window.getInstance().getHeight());	
	}
	
	public void getTerrainPosition(){
		
		if (isActive() && Input.isButtonDown(0)){
			Vec3f pos = new Vec3f(0,0,0);
			Vec2f screenPos = Input.getMousePos();
			Window.getInstance().getSceneDepthmap().bind();
			glGetTexImage(GL_TEXTURE_2D,0,GL_DEPTH_COMPONENT,GL_FLOAT,depthmapBuffer);
			float depth = depthmapBuffer.get((int) (Window.getInstance().getWidth() * screenPos.getY() + screenPos.getX()));
			
			// window coords
			Vec2f w = new Vec2f(screenPos.getX()/Window.getInstance().getWidth(),
								screenPos.getY()/Window.getInstance().getHeight());
			//ndc coords
			Vec3f ndc = new Vec3f(w.getX() * 2 - 1, w.getY() * 2 - 1, depth);
			float cw = Camera.getInstance().getProjectionMatrix().get(3,2) / (ndc.getZ() - Camera.getInstance().getProjectionMatrix().get(2,2)); 
			Vec3f clip = ndc.mul(cw);
			Quaternion clipPos = new Quaternion(clip.getX(),clip.getY(),clip.getZ(),cw);
			Quaternion worldPos =  Camera.getInstance().getViewProjectionMatrix().invert().mul(clipPos);
			worldPos = worldPos.div(worldPos.getW());
		
			pos.setX(worldPos.getX());
			pos.setY(worldPos.getY());
			pos.setZ(worldPos.getZ());
			
			System.out.println(pos);
		}
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}
