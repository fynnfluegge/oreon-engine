package engine.core;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL11.GL_RGBA8;

import java.awt.Canvas;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.opengl.ImageIOImageData;

import engine.buffers.Framebuffer;
import engine.texturing.Texture;

public class Window {
	
	private static Window instance = null;
	
	private Framebuffer multisampledFbo;
	private Framebuffer fbo;
	private Texture sceneTexture;
	private Texture sceneDepthmap;

	
	public static Window getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Window();
	    }
	      return instance;
	}
	
	public void init(){
		
		multisampledFbo = new Framebuffer();
		multisampledFbo.bind();
		multisampledFbo.createColorBufferMultisampleAttachment(8);
		multisampledFbo.createDepthBufferMultisampleAttachment(8);
		multisampledFbo.checkStatus();
		multisampledFbo.unbind();
		
		fbo = new Framebuffer();
		setSceneTexture(new Texture());
		getSceneTexture().generate();
		getSceneTexture().bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, getWidth(), getHeight(), 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		setSceneDepthmap(new Texture());
		getSceneDepthmap().generate();
		getSceneDepthmap().bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, getWidth(), getHeight(), 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		fbo.bind();
		fbo.setDrawBuffer(0);
		fbo.createColorTextureAttachment(getSceneTexture().getId(),0);
		fbo.createDepthTextureAttachment(getSceneDepthmap().getId());
		fbo.checkStatus();
		fbo.unbind();
	}
	
	public void create(int width, int height, String title)
	{
		Display.setTitle(title);
		try {
			DisplayMode displayMode = null;
	        DisplayMode[] modes = Display.getAvailableDisplayModes();

	         for (int i = 0; i < modes.length; i++)
	         {
	             if (modes[i].getWidth() == width
	            	&& modes[i].getHeight() == height
	            	&& modes[i].isFullscreenCapable())
	             {
	            	 	displayMode = modes[i];
	             }
	         }
	       
			Display.setDisplayMode(displayMode);
			Display.setIcon(new ByteBuffer[] {
					new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/logo/oreon_lwjgl_icon16.png")), false, false, null),
                    new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/logo/oreon_lwjgl_icon32.png")), false, false, null)
                    });
			Display.create();
			Keyboard.create();
			Mouse.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void embed(int width, int height, Canvas canvas)
	{
		try {
			Display.setParent(canvas);
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create();
			Keyboard.create();
			Mouse.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public void render()
	{
		Display.update();
	}
	
	public void dispose()
	{
		Display.destroy();
		Keyboard.destroy();
		Mouse.destroy();
	}
	
	public void blitMultisampledFBO(){
		multisampledFbo.blitFrameBuffer(fbo.getId());
	}
	
	public boolean isCloseRequested()
	{
		return Display.isCloseRequested();
	}
	
	public int getWidth()
	{
		return Display.getDisplayMode().getWidth();
	}
	
	public int getHeight()
	{
		return Display.getDisplayMode().getHeight();
	}
	
	public String getTitle()
	{
		return Display.getTitle();
	}
	
	public Texture getSceneTexture() {
		return sceneTexture;
	}
	public void setSceneTexture(Texture sceneTexture) {
		this.sceneTexture = sceneTexture;
	}
	public Texture getSceneDepthmap() {
		return sceneDepthmap;
	}
	public void setSceneDepthmap(Texture sceneDepthmap) {
		this.sceneDepthmap = sceneDepthmap;
	}
	public Framebuffer getFBO() {
		return fbo;
	}
	public void setFBO(Framebuffer fBO) {
		fbo = fBO;
	}
	public Framebuffer getMultisampledFbo() {
		return multisampledFbo;
	}
	public void setMultisampledFbo(Framebuffer multisampledFbo) {
		this.multisampledFbo = multisampledFbo;
	}
}
