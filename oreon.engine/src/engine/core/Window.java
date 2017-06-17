package engine.core;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

import java.awt.Canvas;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.opengl.ImageIOImageData;

import engine.buffers.Framebuffer;
import engine.textures.Texture2D;
import engine.utils.BufferUtil;
import engine.utils.Constants;


public class Window {
	
	private static Window instance = null;
	
	private Framebuffer multisampledFbo;
	private Framebuffer fbo;
	private Texture2D sceneTexture;
	private Texture2D blackScene4LightScatteringTexture;
	private Texture2D sceneDepthmap;

	
	public static Window getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new Window();
	    }
	      return instance;
	}
	
	public void init(){
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(2);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.flip();
		
		multisampledFbo = new Framebuffer();
		multisampledFbo.bind();
		multisampledFbo.createColorBufferMultisampleAttachment(Constants.MULTISAMPLES, 0);
		multisampledFbo.createColorBufferMultisampleAttachment(Constants.MULTISAMPLES, 1);
		multisampledFbo.createDepthBufferMultisampleAttachment(Constants.MULTISAMPLES);
		multisampledFbo.setDrawBuffers(drawBuffers);
		multisampledFbo.checkStatus();
		multisampledFbo.unbind();
		
		sceneTexture = new Texture2D();
		getSceneTexture().generate();
		getSceneTexture().bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, getWidth(), getHeight(), 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		getSceneTexture().bilinearFilter();
		getSceneTexture().clampToEdge();
		
		blackScene4LightScatteringTexture = new Texture2D();
		getBlackScene4LightScatteringTexture().generate();
		getBlackScene4LightScatteringTexture().bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, getWidth(), getHeight(), 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		getBlackScene4LightScatteringTexture().bilinearFilter();
		getBlackScene4LightScatteringTexture().clampToEdge();
		
		sceneDepthmap = new Texture2D();
		getSceneDepthmap().generate();
		getSceneDepthmap().bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, getWidth(), getHeight(), 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		getSceneDepthmap().bilinearFilter();
		getSceneDepthmap().clampToEdge();
		
		fbo = new Framebuffer();
		fbo.bind();
		fbo.createColorTextureAttachment(getSceneTexture().getId(),0);
		fbo.createColorTextureAttachment(getBlackScene4LightScatteringTexture().getId(),1);
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
	
	public void blitMultisampledFBO(int dest, int src){
		multisampledFbo.blitFrameBuffer(dest,src,fbo.getId());
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
	
	public Texture2D getSceneTexture() {
		return sceneTexture;
	}

	public Texture2D getSceneDepthmap() {
		return sceneDepthmap;
	}

	public Framebuffer getFBO() {
		return fbo;
	}

	public Framebuffer getMultisampledFbo() {
		return multisampledFbo;
	}

	public Texture2D getBlackScene4LightScatteringTexture() {
		return blackScene4LightScatteringTexture;
	}

	public void setBlackScene4LightScatteringTexture(Texture2D texture) {
		this.blackScene4LightScatteringTexture = texture;
	}
}
