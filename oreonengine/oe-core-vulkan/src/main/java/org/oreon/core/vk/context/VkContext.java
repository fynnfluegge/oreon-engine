package org.oreon.core.vk.context;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import java.nio.ByteBuffer;

import org.oreon.core.context.EngineContext;
import org.oreon.core.vk.platform.VkCamera;
import org.oreon.core.vk.platform.VkWindow;
import org.oreon.core.vk.util.VkUtil;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class VkContext extends EngineContext{
	
	public static void initialize(){
		
		context = new ClassPathXmlApplicationContext("vk-context.xml");
		registerObject(new VkWindow());
		registerObject(new VkCamera());
		
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		
		if (!glfwVulkanSupported()) {
            throw new AssertionError("GLFW failed to find the Vulkan loader");
        }
		
		ByteBuffer[] layers = {
            	memUTF8("VK_LAYER_LUNARG_standard_validation"),
		};
		
	    VulkanInstance vulkanInstance = new VulkanInstance(
	    		VkUtil.getValidationLayerNames(
	    				Integer.valueOf(getConfig().getProperties().getProperty("validation.enable")) == 1 ? true : false,
	    				layers));
	    VkContext.registerObject(vulkanInstance);
	    
	    getWindow().create();
	}
	
	public static VkWindow getWindow(){
		
		return context.getBean(VkWindow.class);
	}
	
	public static VkCamera getCamera(){
		
		return context.getBean(VkCamera.class);
	}
	
	public static VkResources getResources(){
		
		return context.getBean(VkResources.class);
	}
	
	public static DeviceManager getDeviceManager(){
		
		return (DeviceManager) context.getBean("DeviceManager");
	}
	
	public static VulkanInstance getVulkanInstance(){
		
		return context.getBean(VulkanInstance.class);
	}
	
}
