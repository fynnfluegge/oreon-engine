package org.oreon.core.vk.core.context;

import org.oreon.core.context.EngineContext;
import org.oreon.core.vk.core.device.LogicalDevice;
import org.oreon.core.vk.core.device.PhysicalDevice;
import org.oreon.core.vk.core.platform.VkCamera;
import org.oreon.core.vk.core.platform.VkWindow;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class VkContext extends EngineContext{
	
	public static void initialize(){
		
		context = new ClassPathXmlApplicationContext("vk-context.xml");
		registerObject(new VkWindow());
		registerObject(new VkCamera());
	}
	
	public static VkWindow getWindow(){
		
		return context.getBean(VkWindow.class);
	}
	
	public static VkCamera getCamera(){
		
		return context.getBean(VkCamera.class);
	}
	
	public static VkRenderState getRenderState(){
		
		return context.getBean(VkRenderState.class);
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
	
}
