package org.oreon.system.vk.desktop;

import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.vulkan.EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME;
import static org.lwjgl.vulkan.EXTDebugReport.VK_DEBUG_REPORT_ERROR_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugReport.VK_DEBUG_REPORT_WARNING_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugReport.VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT;
import static org.lwjgl.vulkan.EXTDebugReport.vkCreateDebugReportCallbackEXT;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_GRAPHICS_BIT;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreateInstance;
import static org.lwjgl.vulkan.VK10.vkEnumeratePhysicalDevices;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceQueueFamilyProperties;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceMemoryProperties;
import static org.lwjgl.vulkan.VK10.vkCreateDevice;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.VK_MAKE_VERSION;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkDebugReportCallbackCreateInfoEXT;
import org.lwjgl.vulkan.VkDebugReportCallbackEXT;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.oreon.core.buffers.Framebuffer;
import org.oreon.core.math.Quaternion;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.RenderEngine;
import org.oreon.core.texture.Texture;

public class VkRenderEngine implements RenderEngine{
	
	private VkInstance vkInstance;
	
	private static ByteBuffer[] layers = {
	            	memUTF8("VK_LAYER_LUNARG_standard_validation"),
				};
	 
	private final boolean validation = Boolean.parseBoolean(System.getProperty("vulkan.validation", "false"));
	
	private class DeviceAndGraphicsQueueFamily {
        VkDevice device;
        int queueFamilyIndex;
        VkPhysicalDeviceMemoryProperties memoryProperties;
    }
	
	@Override
	public void init() {
		
		 if (!glfwVulkanSupported()) {
	            throw new AssertionError("GLFW failed to find the Vulkan loader");
	        }
		
		PointerBuffer requiredExtensions = glfwGetRequiredInstanceExtensions();
        if (requiredExtensions == null) {
            throw new AssertionError("Failed to find list of required Vulkan extensions");
        }
        
        vkInstance = createInstance(requiredExtensions); 
        
        VkDebugReportCallbackEXT debugCallback = new VkDebugReportCallbackEXT() {
            public int invoke(int flags, int objectType, long object, long location, int messageCode, long pLayerPrefix, long pMessage, long pUserData) {
                System.err.println("ERROR OCCURED: " + VkDebugReportCallbackEXT.getString(pMessage));
                return 0;
            }
        };
        long debugCallbackHandle = setupDebugging(vkInstance, VK_DEBUG_REPORT_ERROR_BIT_EXT | VK_DEBUG_REPORT_WARNING_BIT_EXT, debugCallback);
        
        final VkPhysicalDevice physicalDevice = getFirstPhysicalDevice(vkInstance);
        final DeviceAndGraphicsQueueFamily deviceAndGraphicsQueueFamily = createDeviceAndGetGraphicsQueueFamily(physicalDevice);
        final VkDevice device = deviceAndGraphicsQueueFamily.device;
        int queueFamilyIndex = deviceAndGraphicsQueueFamily.queueFamilyIndex;
        final VkPhysicalDeviceMemoryProperties memoryProperties = deviceAndGraphicsQueueFamily.memoryProperties;
	
		LongBuffer pSurface = memAllocLong(1);
	    int err = glfwCreateWindowSurface(vkInstance, CoreSystem.getInstance().getWindow().getId(), null, pSurface);
	    
	    long surface = pSurface.get(0);
	    if (err != VK_SUCCESS) {
	        throw new AssertionError("Failed to create surface: ");// + translateVulkanResult(err));
	    }
	}
    

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
	private VkInstance createInstance(PointerBuffer requiredExtensions) {
        VkApplicationInfo appInfo = VkApplicationInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                .pApplicationName(memUTF8("GLFW Vulkan Demo"))
                .pEngineName(memUTF8(""))
                .apiVersion(VK_MAKE_VERSION(1, 0, 2));
        PointerBuffer ppEnabledExtensionNames = memAllocPointer(requiredExtensions.remaining() + 1);
        ppEnabledExtensionNames.put(requiredExtensions);
        ByteBuffer VK_EXT_DEBUG_REPORT_EXTENSION = memUTF8(VK_EXT_DEBUG_REPORT_EXTENSION_NAME);
        ppEnabledExtensionNames.put(VK_EXT_DEBUG_REPORT_EXTENSION);
        ppEnabledExtensionNames.flip();
        PointerBuffer ppEnabledLayerNames = memAllocPointer(layers.length);
        for (int i = 0; validation && i < layers.length; i++)
            ppEnabledLayerNames.put(layers[i]);
        ppEnabledLayerNames.flip();
        VkInstanceCreateInfo pCreateInfo = VkInstanceCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                .pNext(0)
                .pApplicationInfo(appInfo)
                .ppEnabledExtensionNames(ppEnabledExtensionNames)
                .ppEnabledLayerNames(ppEnabledLayerNames);
        PointerBuffer pInstance = memAllocPointer(1);
        int err = vkCreateInstance(pCreateInfo, null, pInstance);
        long instance = pInstance.get(0);
        memFree(pInstance);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create VkInstance: ");// + translateVulkanResult(err));
        }
        VkInstance ret = new VkInstance(instance, pCreateInfo);
        pCreateInfo.free();
        memFree(ppEnabledLayerNames);
        memFree(VK_EXT_DEBUG_REPORT_EXTENSION);
        memFree(ppEnabledExtensionNames);
        memFree(appInfo.pApplicationName());
        memFree(appInfo.pEngineName());
        appInfo.free();
        return ret;
    }
	
	private long setupDebugging(VkInstance instance, int flags, VkDebugReportCallbackEXT callback) {
        VkDebugReportCallbackCreateInfoEXT dbgCreateInfo = VkDebugReportCallbackCreateInfoEXT.calloc()
                .sType(VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT)
                .pNext(0)
                .pfnCallback(callback)
                .pUserData(0)
                .flags(flags);
        LongBuffer pCallback = memAllocLong(1);
        int err = vkCreateDebugReportCallbackEXT(instance, dbgCreateInfo, null, pCallback);
        long callbackHandle = pCallback.get(0);
        memFree(pCallback);
        dbgCreateInfo.free();
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create VkInstance: ");// + translateVulkanResult(err));
        }
        return callbackHandle;
    }
	
	private static VkPhysicalDevice getFirstPhysicalDevice(VkInstance instance) {
        IntBuffer pPhysicalDeviceCount = memAllocInt(1);
        int err = vkEnumeratePhysicalDevices(instance, pPhysicalDeviceCount, null);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get number of physical devices: ");// + translateVulkanResult(err));
        }
        PointerBuffer pPhysicalDevices = memAllocPointer(pPhysicalDeviceCount.get(0));
        err = vkEnumeratePhysicalDevices(instance, pPhysicalDeviceCount, pPhysicalDevices);
        long physicalDevice = pPhysicalDevices.get(0);
        memFree(pPhysicalDeviceCount);
        memFree(pPhysicalDevices);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get physical devices: ");// + translateVulkanResult(err));
        }
        return new VkPhysicalDevice(physicalDevice, instance);
    }
	
	private DeviceAndGraphicsQueueFamily createDeviceAndGetGraphicsQueueFamily(VkPhysicalDevice physicalDevice) {
        IntBuffer pQueueFamilyPropertyCount = memAllocInt(1);
        vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueFamilyPropertyCount, null);
        int queueCount = pQueueFamilyPropertyCount.get(0);
        VkQueueFamilyProperties.Buffer queueProps = VkQueueFamilyProperties.calloc(queueCount);
        vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueFamilyPropertyCount, queueProps);
        memFree(pQueueFamilyPropertyCount);
        int graphicsQueueFamilyIndex;
        for (graphicsQueueFamilyIndex = 0; graphicsQueueFamilyIndex < queueCount; graphicsQueueFamilyIndex++) {
            if ((queueProps.get(graphicsQueueFamilyIndex).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0)
                break;
        }
        queueProps.free();
        FloatBuffer pQueuePriorities = memAllocFloat(1).put(0.0f);
        pQueuePriorities.flip();
        VkDeviceQueueCreateInfo.Buffer queueCreateInfo = VkDeviceQueueCreateInfo.calloc(1)
                .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                .queueFamilyIndex(graphicsQueueFamilyIndex)
                .pQueuePriorities(pQueuePriorities);

        PointerBuffer extensions = memAllocPointer(1);
        ByteBuffer VK_KHR_SWAPCHAIN_EXTENSION = memUTF8(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
        extensions.put(VK_KHR_SWAPCHAIN_EXTENSION);
        extensions.flip();
        PointerBuffer ppEnabledLayerNames = memAllocPointer(layers.length);
        for (int i = 0; validation && i < layers.length; i++)
            ppEnabledLayerNames.put(layers[i]);
        ppEnabledLayerNames.flip();

        VkDeviceCreateInfo deviceCreateInfo = VkDeviceCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                .pNext(0)
                .pQueueCreateInfos(queueCreateInfo)
                .ppEnabledExtensionNames(extensions)
                .ppEnabledLayerNames(ppEnabledLayerNames);

        PointerBuffer pDevice = memAllocPointer(1);
        int err = vkCreateDevice(physicalDevice, deviceCreateInfo, null, pDevice);
        long device = pDevice.get(0);
        memFree(pDevice);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create device: ");// + translateVulkanResult(err));
        }

        VkPhysicalDeviceMemoryProperties memoryProperties = VkPhysicalDeviceMemoryProperties.calloc();
        vkGetPhysicalDeviceMemoryProperties(physicalDevice, memoryProperties);

        DeviceAndGraphicsQueueFamily ret = new DeviceAndGraphicsQueueFamily();
        ret.device = new VkDevice(device, physicalDevice, deviceCreateInfo);
        ret.queueFamilyIndex = graphicsQueueFamilyIndex;
        ret.memoryProperties = memoryProperties;

        deviceCreateInfo.free();
        memFree(ppEnabledLayerNames);
        memFree(VK_KHR_SWAPCHAIN_EXTENSION);
        memFree(extensions);
        memFree(pQueuePriorities);
        return ret;
    }

	@Override
	public boolean isGrid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCameraUnderWater() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWaterReflection() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWaterRefraction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBloomEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Framebuffer getMultisampledFbo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Framebuffer getDeferredFbo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Texture getSceneDepthmap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quaternion getClipplane() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getSightRangeFactor() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getUnderwater() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setClipplane(Quaternion plane) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGrid(boolean flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWaterRefraction(boolean flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWaterReflection(boolean flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCameraUnderWater(boolean flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSightRangeFactor(float range) {
		// TODO Auto-generated method stub
		
	}

}
