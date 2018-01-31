package org.oreon.core.vk.util;

import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkEnumerateInstanceExtensionProperties;
import static org.lwjgl.vulkan.VK10.vkEnumerateInstanceLayerProperties;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceFeatures;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceProperties;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceQueueFamilyProperties;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkExtensionProperties;
import org.lwjgl.vulkan.VkLayerProperties;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

public class DeviceCapabilities {
	
	public static void checkExtensionSupport(PointerBuffer ppEnabledExtensionNames){
		
		IntBuffer extensionCount = memAllocInt(1);
		
		int err = vkEnumerateInstanceExtensionProperties("", extensionCount, null);
		if (err != VK_SUCCESS) {
            throw new AssertionError(VKUtil.translateVulkanResult(err));
        }
		
		VkExtensionProperties.Buffer extensions = VkExtensionProperties.calloc(extensionCount.get(0));
				
		err = vkEnumerateInstanceExtensionProperties("", extensionCount, extensions);
		if (err != VK_SUCCESS) {
            throw new AssertionError(VKUtil.translateVulkanResult(err));
        }
		
		List<String> availableExtensions = new ArrayList<>();
		for (VkExtensionProperties extension : extensions){
			availableExtensions.add(extension.extensionNameString());
		}
		
		for (int i=0; i<ppEnabledExtensionNames.limit(); i++){
			if (!availableExtensions.contains(ppEnabledExtensionNames.getStringUTF8())){
				throw new AssertionError("Extension " + ppEnabledExtensionNames.getStringUTF8() + " not supported");
			}
		}
		
		ppEnabledExtensionNames.flip();
		
		memFree(extensionCount);
		extensions.free();
	}
	
	public static void checkValidationLayerSupport(PointerBuffer ppEnabledLayerNames){
		
		IntBuffer layerCount = memAllocInt(1);
		
		int err = vkEnumerateInstanceLayerProperties(layerCount, null);
		if (err != VK_SUCCESS) {
            throw new AssertionError(VKUtil.translateVulkanResult(err));
        }
		
		VkLayerProperties.Buffer layers = VkLayerProperties.calloc(layerCount.get(0));
		
		err = vkEnumerateInstanceLayerProperties(layerCount, layers);
		if (err != VK_SUCCESS) {
            throw new AssertionError(VKUtil.translateVulkanResult(err));
        }
		
		List<String> availableLayers = new ArrayList<>();
		for (VkLayerProperties layer : layers){
			availableLayers.add(layer.layerNameString());
		}
		
		for (int i=0; i<ppEnabledLayerNames.limit(); i++){
			if (!availableLayers.contains(ppEnabledLayerNames.getStringUTF8())){
				throw new AssertionError("Extension " + ppEnabledLayerNames.getStringUTF8() + " not supported");
			}
		}

		ppEnabledLayerNames.flip();
		
		memFree(layerCount);
		layers.free();
	}
	
	public static void checkPhysicalDeviceProperties(VkPhysicalDevice physicalDevice){
		
		VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.create();
		vkGetPhysicalDeviceProperties(physicalDevice, properties);
		
		System.out.println(properties.deviceNameString());
	}
	
	public static void checkPhysicalDeviceFeatures(VkPhysicalDevice physicalDevice){
		
		VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.create();
		vkGetPhysicalDeviceFeatures(physicalDevice, features);
	}

}
