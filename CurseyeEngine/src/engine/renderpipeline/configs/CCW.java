package engine.renderpipeline.configs;

import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.glFrontFace;
import engine.renderpipeline.RenderingConfig;

public class CCW implements RenderingConfig{
	
	public void enable(){
		glFrontFace(GL_CCW);
	}

	public void disable(){
		glFrontFace(GL_CW);
	}
}
