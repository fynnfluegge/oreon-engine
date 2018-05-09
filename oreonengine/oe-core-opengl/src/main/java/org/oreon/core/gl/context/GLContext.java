package org.oreon.core.gl.context;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.platform.GLCamera;
import org.oreon.core.gl.platform.GLWindow;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GLContext extends EngineContext{
	
	public static void initialize(){
		
		context = new ClassPathXmlApplicationContext("gl-context.xml");
		registerObject(new GLWindow());
		registerObject(new GLCamera());
	}
	
	public static GLWindow getWindow(){
		
		return context.getBean(GLWindow.class);
	}
	
	public static GLCamera getCamera(){
		
		return context.getBean(GLCamera.class);
	}
	
	public static GLRenderState getRenderState(){
		
		return context.getBean(GLRenderState.class);
	}

}
