package org.oreon.core.vk.core.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class VkContext {

private static ApplicationContext context;
	
	public static void initialize(){
		context = new ClassPathXmlApplicationContext("vk-context.xml");
	}
	
	public static VkRenderContext getRenderContext(){
		
		return context.getBean(VkRenderContext.class);
	}
	
	public static VkDeviceContext getDeviceContext(){
		
		return context.getBean(VkDeviceContext.class);
	}
	
}
