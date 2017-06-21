package org.oreon.engine.engine.configs;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;


public class AdditiveBlending implements RenderConfig{
	
	private float alpha;
	
	public AdditiveBlending(float alpha){
		this.setAlpha(alpha);
	}
	
	public void enable(){
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);	
		glBlendFunc(GL_SRC_ALPHA, GL_ONE);
	}
	
	public void disable(){
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_BLEND);
	}
	
	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
}
