package org.oreon.core.gl.config;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

public class AlphaBlendingOneMinusSrcAlpha implements RenderConfig{
	
	public AlphaBlendingOneMinusSrcAlpha(){
	}
	
	public void enable(){
		glEnable(GL_BLEND);	
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void disable(){
		glDisable(GL_BLEND);
	}
}
