package org.oreon.core.gl.context;

import static org.lwjgl.glfw.GLFW.glfwInit;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;
import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.platform.GLCamera;
import org.oreon.core.gl.platform.GLWindow;
import org.oreon.core.gl.util.GLUtil;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import lombok.extern.log4j.Log4j;

@Log4j
public class GLContext extends EngineContext{
	
	public static void initialize(){
		
		context = new ClassPathXmlApplicationContext("gl-context.xml");
		registerObject(new GLWindow());
		registerObject(new GLCamera());

		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// create OpenGL Context
		getWindow().create();
		
		log.info("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION) + " bytes");
		log.info("Max Geometry Uniform Blocks: " + GL11.glGetInteger(GL31.GL_MAX_GEOMETRY_UNIFORM_BLOCKS));
		log.info("Max Geometry Shader Invocations: " + GL11.glGetInteger(GL40.GL_MAX_GEOMETRY_SHADER_INVOCATIONS));
		log.info("Max Uniform Buffer Bindings: " + GL11.glGetInteger(GL31.GL_MAX_UNIFORM_BUFFER_BINDINGS));
		log.info("Max Uniform Block Size: " + GL11.glGetInteger(GL31.GL_MAX_UNIFORM_BLOCK_SIZE) + " bytes");
		log.info("Max SSBO Block Size: " + GL11.glGetInteger(GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE) + " bytes");	
		log.info("Max Image Bindings: " + GL11.glGetInteger(GL42.GL_MAX_IMAGE_UNITS));

		GLUtil.initialize();
	}
	
	public static GLWindow getWindow(){
		
		return context.getBean(GLWindow.class);
	}
	
	public static GLCamera getCamera(){
		
		return context.getBean(GLCamera.class);
	}
	
	public static GLResources getResources(){
		
		return context.getBean(GLResources.class);
	}

}
