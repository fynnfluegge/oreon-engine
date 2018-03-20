package org.oreon.core.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EngineContext {
	
	private static ApplicationContext context;
	
	public static void initialize(){
		context = new ClassPathXmlApplicationContext("application-context.xml");
	}
	
	public static CommonConfig getCommonConfig(){
		
		return context.getBean(CommonConfig.class);
	}

}
