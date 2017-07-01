package engine.core;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL;

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
	
	private long window;
	private int width;
	private int height;
	
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
		setWidth(width);
		setHeight(height);
		
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);	
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);	
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);	
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);	
		
		window = glfwCreateWindow(width, height, "oreon engine", 0, 0);
		
		if(window == 0) {
		    throw new RuntimeException("Failed to create window");
		}
		
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		glfwShowWindow(window);
	}
	
//	public void embed(int width, int height, Canvas canvas)
//	{
//		try {
//			Display.setParent(canvas);
//			Display.setDisplayMode(new DisplayMode(width, height));
//			Display.create();
//			Keyboard.create();
//			Mouse.create();
//		} catch (LWJGLException e) {
//			e.printStackTrace();
//		}
//	}
	
	public void render()
	{
		glfwSwapBuffers(window);
	}
	
	public void dispose()
	{
		glfwDestroyWindow(window);
//		Keyboard.destroy();
//		Mouse.destroy();
	}
	
	public void blitMultisampledFBO(int dest, int src){
		multisampledFbo.blitFrameBuffer(dest,src,fbo.getId());
	}
	
	public boolean isCloseRequested()
	{
		return glfwWindowShouldClose(window);
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
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
	
	public long getWindow() {
		return window;
	}

	public void setWindow(long window) {
		this.window = window;
	}
}
