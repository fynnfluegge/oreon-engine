package org.oreon.core.vk.context;

import org.oreon.core.context.EngineContext;
import org.oreon.core.vk.platform.VkCamera;
import org.oreon.core.vk.platform.VkWindow;
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
	
	public static DeviceManager getDeviceManager(){
		
		return (DeviceManager) context.getBean("DeviceManager");
	}
	
	public static VulkanInstance getVulkanInstance(){
		
		return context.getBean(VulkanInstance.class);
	}
	
}
