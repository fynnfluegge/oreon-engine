package org.oreon.core.gl.config;

import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.glFrontFace;

import org.oreon.core.configs.RenderConfig;

public class CCW implements RenderConfig{
	
	public void enable(){
		glFrontFace(GL_CCW);
	}

	public void disable(){
		glFrontFace(GL_CW);
	}
}
