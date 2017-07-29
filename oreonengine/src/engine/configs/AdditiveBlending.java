package engine.configs;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;


public class AdditiveBlending implements RenderConfig{
	
	public AdditiveBlending(){
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
}
