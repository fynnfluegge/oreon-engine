package org.oreon.core.gl.context;

import org.oreon.core.gl.platform.GLWindow;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GLContext {

	private static ApplicationContext context;
	
	public static void initialize(){
		context = new ClassPathXmlApplicationContext("gl-context.xml");
	}
	
	public static GLRenderContext getRenderContext(){
		
		return context.getBean(GLRenderContext.class);
	}
	
	public static GLWindow getWindow(){
		
		return context.getBean(GLWindow.class);
	}
}
