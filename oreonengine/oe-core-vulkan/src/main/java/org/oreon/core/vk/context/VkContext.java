package org.oreon.core.vk.context;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import java.nio.ByteBuffer;

import org.oreon.core.context.EngineContext;
import org.oreon.core.vk.platform.VkWindow;
import org.oreon.core.vk.scenegraph.VkCamera;
import org.oreon.core.vk.util.VkUtil;

import lombok.Getter;

public class VkContext extends EngineContext{
	
	@Getter
	private static ByteBuffer[] enabledLayers;
	@Getter
	private static VulkanInstance vkInstance;
	@Getter
	private static VkResources resources;
	@Getter
	private static DeviceManager deviceManager;
	
	public static void initialize(){
		
		init();
		
		window = new VkWindow();
		camera = new VkCamera();
		resources = new VkResources();
		deviceManager = new DeviceManager();
		
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");
		
		if (!glfwVulkanSupported()) {
            throw new AssertionError("GLFW failed to find the Vulkan loader");
        }
		
		ByteBuffer[] layers = {
            	memUTF8("VK_LAYER_LUNARG_standard_validation")
//            	memUTF8("VK_LAYER_LUNARG_assistant_layer")
		};
		
		enabledLayers = layers;
		
	    vkInstance = new VulkanInstance(
	    		VkUtil.getValidationLayerNames(
	    				Integer.valueOf(getConfig().getProperties().getProperty("validation.enable")) == 1 ? true : false,
	    				layers));
	    
	    getWindow().create();
	}
	
	public static VkCamera getCamera(){
		
		return (VkCamera) camera;
	}
	
	public static VkWindow getWindow(){
		
		return (VkWindow) window;
	}
	
}
