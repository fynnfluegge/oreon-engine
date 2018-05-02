package org.oreon.core.gl.context;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.platform.GLCamera;
import org.oreon.core.gl.platform.GLWindow;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GLContext {

	private static ApplicationContext context;
	
	public static void initialize(){
		context = new ClassPathXmlApplicationContext("gl-context.xml");
		EngineContext.registerWindow(new GLWindow());
		EngineContext.registerCamera(new GLCamera());
	}
	
	public static GLWindow getWindow(){
		
		return context.getBean(GLWindow.class);
	}
	
	public static GLRenderContext getRenderContext(){
		
		return context.getBean(GLRenderContext.class);
	}
	
	public static <T> T getObject(Class<T> clazz){
		
		return context.getBean(clazz);
	}
	
	public static void registerObject(Object instance){

		ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
		beanFactory.registerSingleton(instance.getClass().getCanonicalName(), instance);
	}

}
