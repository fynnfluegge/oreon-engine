package engine.renderpipeline.configs;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import engine.renderpipeline.RenderingConfig;

public class CullFaceDisable implements RenderingConfig{
	
	public void enable(){
		glDisable(GL_CULL_FACE);
	}
	
	public void disable(){
		glEnable(GL_CULL_FACE);
	}		
}