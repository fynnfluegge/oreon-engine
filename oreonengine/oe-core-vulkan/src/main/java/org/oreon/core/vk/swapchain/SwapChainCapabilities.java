package org.oreon.core.vk.swapchain;

import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

import java.nio.IntBuffer;

import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.oreon.core.vk.util.VKUtil;

public class SwapChainCapabilities {
	
	private VkSurfaceCapabilitiesKHR surfaceCapabilities;
	private VkSurfaceFormatKHR.Buffer surfaceFormats;
	private IntBuffer presentModes;
	
	public SwapChainCapabilities(VkPhysicalDevice physicalDevice, long surface) {

		surfaceCapabilities = VkSurfaceCapabilitiesKHR.calloc();
        int err = vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surface, surfaceCapabilities);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get physical device surface capabilities: " + VKUtil.translateVulkanResult(err));
        }
        
        IntBuffer pFormatCount = memAllocInt(1);
        err = vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pFormatCount, null);
        int formatCount = pFormatCount.get(0);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to query number of physical device surface formats: " + VKUtil.translateVulkanResult(err));
        }

        surfaceFormats = VkSurfaceFormatKHR.calloc(formatCount);
        err = vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pFormatCount, surfaceFormats);
        memFree(pFormatCount);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to query physical device surface formats: " + VKUtil.translateVulkanResult(err));
        }
        
        IntBuffer pPresentModeCount = memAllocInt(1);
        err = vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, pPresentModeCount, null);
        int presentModeCount = pPresentModeCount.get(0);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get number of physical device surface presentation modes: " + VKUtil.translateVulkanResult(err));
        }

        presentModes = memAllocInt(presentModeCount);
        err = vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, pPresentModeCount, presentModes);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get physical device surface presentation modes: " + VKUtil.translateVulkanResult(err));
        }
        
        memFree(pPresentModeCount);
        memFree(pFormatCount);
	}
	
	public VkSurfaceCapabilitiesKHR getSurfaceCapabilities() {
		return surfaceCapabilities;
	}
	public VkSurfaceFormatKHR.Buffer getSurfaceFormats() {
		return surfaceFormats;
	}
	public IntBuffer getPresentModes() {
		return presentModes;
	}
	
}
