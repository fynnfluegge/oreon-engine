package org.oreon.core.context;

import org.oreon.core.platform.Camera;
import org.oreon.core.platform.GLFWInput;
import org.oreon.core.platform.Window;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

public abstract class EngineContext {
	
	protected static ConfigurableApplicationContext context;
	
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
	
	public static RenderState getRenderState(){
		
		return context.getBean(RenderState.class);
	}
	
	public static <T> T getObject(Class<T> clazz){
		
		return context.getBean(clazz);
	}
	
	public static void registerObject(Object instance){

		ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
		beanFactory.registerSingleton(instance.getClass().getCanonicalName(), instance);
	}

}
