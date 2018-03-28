package org.oreon.core.gl.context;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.platform.GLCamera;
import org.oreon.core.gl.platform.GLWindow;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GLContext {

	private static ApplicationContext context;
	
	public static void initialize(){
		context = new ClassPathXmlApplicationContext("gl-context.xml");
		EngineContext.registerWindow(new GLWindow());
		EngineContext.registerCamera(new GLCamera());
	}
	
	public static GLRenderContext getRenderContext(){
		
		return context.getBean(GLRenderContext.class);
	}

}
