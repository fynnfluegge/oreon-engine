package org.oreon.core.vk.core.context;

import org.oreon.core.vk.core.device.LogicalDevice;
import org.oreon.core.vk.core.device.PhysicalDevice;
import org.oreon.core.vk.core.platform.VkWindow;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class VkContext {

private static ApplicationContext context;
	
	public static void initialize(){
		context = new ClassPathXmlApplicationContext("vk-context.xml");
	}
	
	public static VkRenderContext getRenderContext(){
		
		return context.getBean(VkRenderContext.class);
	}
	
	public static VkWindow getWindow(){
		
		return context.getBean(VkWindow.class);
	}
	
	public static LogicalDevice getLogicalDevice(){
		
		return context.getBean(LogicalDevice.class);
	}
	
	public static PhysicalDevice getPhysicalDevice(){
		
		return context.getBean(PhysicalDevice.class);
	}
	
	public static VkCamera getVkCamera(){
		
		 return context.getBean(VkCamera.class);
	}
	
	public static void registerInstance(VulkanInstance instance){

		ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
		beanFactory.registerSingleton(instance.getClass().getCanonicalName(), instance);
	}
	
	public static void registerPhysicalDevice(PhysicalDevice device){

		ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
		beanFactory.registerSingleton(device.getClass().getCanonicalName(), device);
	}
	
	public static void registerLogicalDevice(LogicalDevice device){

		ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
		beanFactory.registerSingleton(device.getClass().getCanonicalName(), device);
	}
	
	public static void registerVkCamera(VkCamera camera){

		ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
		beanFactory.registerSingleton(camera.getClass().getCanonicalName(), camera);
	}
	
}
