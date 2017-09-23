package org.oreon.core.gl.config;

import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import org.oreon.core.configs.RenderConfig;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.glClear;

public class WaterConfig implements RenderConfig{

	public void enable() {
		glDisable(GL_CULL_FACE);
	}

	public void disable() {
		glEnable(GL_CULL_FACE);
	}

	public void clearScreenDeepOcean()
	{
		glClearColor(0.1f,0.125f,0.19f,1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
}
