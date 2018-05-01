package org.oreon.core.vk.core.context;

import org.oreon.core.context.EngineContext;
import org.oreon.core.vk.core.device.LogicalDevice;
import org.oreon.core.vk.core.device.PhysicalDevice;
import org.oreon.core.vk.core.platform.VkCamera;
import org.oreon.core.vk.core.platform.VkWindow;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class VkContext {

private static ApplicationContext context;
	
	public static void initialize(){
		
		context = new ClassPathXmlApplicationContext("vk-context.xml");
		EngineContext.registerWindow(new VkWindow());
		EngineContext.registerCamera(new VkCamera());
	}
	
	public static VkWindow getWindow(){
		
		return context.getBean(VkWindow.class);
	}
	
	public static DescriptorPoolManager getDescriptorPoolManager(){
		
		return context.getBean(DescriptorPoolManager.class);
	}
	
	public static LogicalDevice getLogicalDevice(){
		
		return context.getBean(LogicalDevice.class);
	}
	
	public static PhysicalDevice getPhysicalDevice(){
		
		return context.getBean(PhysicalDevice.class);
	}
	
	public static VulkanInstance getVulkanInstance(){
		
		return context.getBean(VulkanInstance.class);
	}
	
	public static <T> T getObject(Class<T> clazz){
		
		return context.getBean(clazz);
	}
	
	public static void registerObject(Object instance){

		ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
		beanFactory.registerSingleton(instance.getClass().getCanonicalName(), instance);
	}
	
}
