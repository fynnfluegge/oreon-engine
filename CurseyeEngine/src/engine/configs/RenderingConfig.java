package engine.configs;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL20.GL_POINT_SPRITE;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;
import static org.lwjgl.opengl.GL32.GL_PROGRAM_POINT_SIZE;

public interface RenderingConfig {

	public void enable();
	
	public void disable();
	
	public static void init(){
		glFrontFace(GL_CW);				
		glEnable(GL_POINT_SPRITE);
		glEnable(GL_CULL_FACE);	
		glCullFace(GL_BACK);
		glEnable(GL_DEPTH_TEST);     	
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_FRAMEBUFFER_SRGB);
		glEnable(GL_PROGRAM_POINT_SIZE);
	}
	
	public static void clearScreen()
	{
		glClearColor(0.0f,0.0f,0.0f,1.0f);
		glClearDepth(1.0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public static void clearScreenDeepOceanRefraction()
	{
		glClearColor(0.02f,0.03f,0.055f,1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public static void clearScreenDeepOceanReflection()
	{
		glClearColor(0.3294f,0.4917f,0.7270f,1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
}
