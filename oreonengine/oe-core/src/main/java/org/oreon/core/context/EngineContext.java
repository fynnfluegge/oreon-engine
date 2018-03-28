package org.oreon.core.context;

import org.oreon.core.platform.Camera;
import org.oreon.core.platform.GLFWInput;
import org.oreon.core.platform.Window;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EngineContext {
	
	private static ConfigurableApplicationContext context;
	
	public static void initialize(){
		context = new ClassPathXmlApplicationContext("application-context.xml");
	}
	
	public static Configuration getConfig(){
		
		return context.getBean(Configuration.class);
	}
	
	public static GLFWInput getInput(){
		
		return context.getBean(GLFWInput.class);
	}
	
	public static Camera getCamera(){
		
		return context.getBean(Camera.class);
	}
	
	public static Window getWindow(){
		
		return context.getBean(Window.class);
	}
	
	public static void registerCamera(Camera camera){

		ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
		beanFactory.registerSingleton(camera.getClass().getCanonicalName(), camera);
	}
	
	public static void registerWindow(Window window){

		ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
		beanFactory.registerSingleton(window.getClass().getCanonicalName(), window);
	}

}
