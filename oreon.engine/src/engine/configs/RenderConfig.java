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
import static org.lwjgl.opengl.GL13.GL_SAMPLE_ALPHA_TO_COVERAGE;
import static org.lwjgl.opengl.GL20.GL_POINT_SPRITE;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;
import static org.lwjgl.opengl.GL32.GL_PROGRAM_POINT_SIZE;
import static org.lwjgl.opengl.GL30.GL_CLIP_DISTANCE0;
import static org.lwjgl.opengl.GL30.GL_CLIP_DISTANCE1;
import static org.lwjgl.opengl.GL30.GL_CLIP_DISTANCE2;
import static org.lwjgl.opengl.GL30.GL_CLIP_DISTANCE3;
import static org.lwjgl.opengl.GL30.GL_CLIP_DISTANCE4;
import static org.lwjgl.opengl.GL30.GL_CLIP_DISTANCE5;

public interface RenderConfig {

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
		glEnable(GL_SAMPLE_ALPHA_TO_COVERAGE);
		glEnable(GL_CLIP_DISTANCE0);
		glEnable(GL_CLIP_DISTANCE1);
		glEnable(GL_CLIP_DISTANCE2);
		glEnable(GL_CLIP_DISTANCE3);
		glEnable(GL_CLIP_DISTANCE4);
		glEnable(GL_CLIP_DISTANCE5);
	}
	
	public static void clearScreen()
	{
		glClearColor(0.0f,0.0f,0.0f,1.0f);
		glClearDepth(1.0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public static void clearScreenDeepOceanRefraction()
	{
		glClearColor(0.015f,0.022f,0.04f,1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public static void clearScreenDeepOceanReflection()
	{
		glClearColor(0.0f,0.0f,0.0f,1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
}
