package org.oreon.core.gl.wrapper.parameter;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import org.oreon.core.gl.pipeline.RenderParameter;

public class AlphaBlendingOneMinusSrcAlpha implements RenderParameter{
	
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
