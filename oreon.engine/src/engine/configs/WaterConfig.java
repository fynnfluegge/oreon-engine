package engine.configs;

import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
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

	public void clearScreenDeepOceanRefraction()
	{
		glClearColor(0.015f,0.022f,0.04f,1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public void clearScreenDeepOceanReflection()
	{
		glClearColor(0.2994f,0.4417f,0.6870f,1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
}
