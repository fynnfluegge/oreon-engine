package engine.core;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
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
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_FUNC;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_MODE;
import static org.lwjgl.opengl.GL30.GL_COMPARE_REF_TO_TEXTURE;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;

import java.nio.ByteBuffer;

import engine.buffers.Framebuffer;
import engine.texturing.Texture;

public class OpenGLDisplay {
	
	private static OpenGLDisplay instance = null;
	
	private Framebuffer fbo;
	private Texture sceneTexture;
	private Texture sceneDepthmap;
	private LwjglWindow lwjglWindow;

	
	public static OpenGLDisplay getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new OpenGLDisplay();
	    }
	      return instance;
	}
	
	protected OpenGLDisplay(){
		lwjglWindow = new LwjglWindow();
	}
	
	
	public void init(){
		
		fbo = new Framebuffer();
		setSceneTexture(new Texture());
		getSceneTexture().generate();
		getSceneTexture().bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, lwjglWindow.getWidth(), lwjglWindow.getHeight(), 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		setSceneDepthmap(new Texture());
		getSceneDepthmap().generate();
		getSceneDepthmap().bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, lwjglWindow.getWidth(), lwjglWindow.getHeight(), 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		fbo.bind();
		fbo.setDrawBuffer(0);
		fbo.colorTextureAttachment(getSceneTexture().getId(), 0);
		fbo.depthTextureAttachment(getSceneDepthmap().getId());
		fbo.checkStatus();
		fbo.unbind();
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

	public LwjglWindow getLwjglWindow() {
		return lwjglWindow;
	}

	public void setLwjglWindow(LwjglWindow lwjglWindow) {
		this.lwjglWindow = lwjglWindow;
	}
}
