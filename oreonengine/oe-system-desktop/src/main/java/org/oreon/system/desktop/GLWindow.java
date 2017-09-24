package org.oreon.system.desktop;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_TRUE;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.oreon.core.gl.texture.ImageLoader;
import org.oreon.core.system.Window;

public class GLWindow extends Window{

	public GLWindow(){}
	
	public void create()
	{
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);	
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);	
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);	
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);	
		
		setId(glfwCreateWindow(getWidth(), getHeight(), "OE2.1", 0, 0));
		
		if(getId() == 0) {
		    throw new RuntimeException("Failed to create window");
		}
		
		ByteBuffer bufferedImage = ImageLoader.loadImageToByteBuffer("textures/logo/oreon_lwjgl_icon32.png");
		
		GLFWImage image = GLFWImage.malloc();
		
		image.set(32, 32, bufferedImage);
		
		GLFWImage.Buffer images = GLFWImage.malloc(1);
        images.put(0, image);
		
		glfwSetWindowIcon(getId(), images);
		
		glfwMakeContextCurrent(getId());
		GL.createCapabilities();
		glfwShowWindow(getId());
	}
	
	public void draw()
	{
		glfwSwapBuffers(getId());
	}
	
	public void dispose()
	{
		glfwDestroyWindow(getId());
	}
	
	public boolean isCloseRequested()
	{
		return glfwWindowShouldClose(getId());
	}
	
	public void resize(int width, int height) {
		glfwSetWindowSize(getId(), width, height);
		setHeight(height);
		setWidth(width);
//		CoreSystem.getInstance().getScenegraph().getCamera().setProjection(70, width, height);
	}
}
