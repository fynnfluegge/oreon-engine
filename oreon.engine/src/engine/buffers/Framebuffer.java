package engine.buffers;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glRenderbufferStorageMultisample;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;
import engine.core.Window;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDrawBuffer;


public class Framebuffer {
	
	private int id;
	
	public Framebuffer(){
		
		id = glGenFramebuffers();
	}
	
	public void bind()
	{
		glBindFramebuffer(GL_FRAMEBUFFER, id);
	}
	
	public void unbind()
	{
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public void setDrawBuffer(int i)
	{
		glDrawBuffer(GL_COLOR_ATTACHMENT0 + i);
	}
	
	public void createColorBufferAttachment(int x, int y, int i)
	{
		int colorbuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, colorbuffer);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_RGBA32F, x, y);
		glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_RENDERBUFFER, colorbuffer);
	}
	

	public void createColorTextureAttachment(int texture, int i)
	{
		glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D, texture, 0);
	}
	
	public void createDepthbufferAttachment(int x, int y)
	{
		int depthBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32F, x, y);
		glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);
	}
	
	public void createDepthTextureAttachment(int texture)
	{
		glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, texture, 0);
	}
	
	public void createColorBufferMultisampleAttachment(int samples){
		int colorBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, colorBuffer);
		glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_RGBA8, 
									       		Window.getInstance().getWidth(), 
									       		Window.getInstance().getHeight());
		glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER,GL_COLOR_ATTACHMENT0,
									GL_RENDERBUFFER,colorBuffer);
	}
	
	public void createDepthBufferMultisampleAttachment(int samples){
		int depthBuffer = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
		glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_DEPTH_COMPONENT32F, 
									       		Window.getInstance().getWidth(), 
									       		Window.getInstance().getHeight());
		glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER,GL_DEPTH_ATTACHMENT,
									GL_RENDERBUFFER,depthBuffer);
	}
	
	public void blitFrameBuffer(int writeFBO){
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, writeFBO);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, id);
		glBlitFramebuffer(0,0,Window.getInstance().getWidth(),Window.getInstance().getHeight(),
						  0,0,Window.getInstance().getWidth(),Window.getInstance().getHeight(),
						  GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
	}
	
	public void checkStatus()
	{
		if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
		{
			System.err.println("Framebuffer creation failed");
			System.exit(1);
		}
	}

	public int genRenderbuffer(){
		
		return glGenRenderbuffers();
	}
	
	public int getId()
	{
		return id;
	}
}
