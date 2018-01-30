package org.oreon.core.vk.queue;

import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceQueueFamilyProperties;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_GRAPHICS_BIT;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_COMPUTE_BIT;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_TRANSFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_SPARSE_BINDING_BIT;

import java.nio.IntBuffer;

import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

public class Queue {
	
	private QueueFamily family;
	private VkQueue vkQueueHandle;

	public Queue() {
		// TODO Auto-generated constructor stub
	}
	
	public void createQueue(){
		
		
	} 
	
	public int getGraphicsQueueIndex(VkPhysicalDevice physicalDevice){
		
		return findQueueFamiliyIndex(physicalDevice, VK_QUEUE_GRAPHICS_BIT);
	}
	
	public int getComputeQueueIndex(VkPhysicalDevice physicalDevice){
		
		return findQueueFamiliyIndex(physicalDevice, VK_QUEUE_COMPUTE_BIT);
	}
	
	public int getTransferQueueIndex(VkPhysicalDevice physicalDevice){
		
		return findQueueFamiliyIndex(physicalDevice, VK_QUEUE_TRANSFER_BIT);
	}
	
	public int getSparseBindingQueueIndex(VkPhysicalDevice physicalDevice){
		
		return findQueueFamiliyIndex(physicalDevice, VK_QUEUE_SPARSE_BINDING_BIT);
	}
	
	public int findQueueFamiliyIndex(VkPhysicalDevice physicalDevice, int queueFlag){
		
		IntBuffer pQueueFamilyPropertyCount = memAllocInt(1);
        vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueFamilyPropertyCount, null);
        int queueCount = pQueueFamilyPropertyCount.get(0);
        
        System.out.println(queueCount + " Queue Families supported");
        
        VkQueueFamilyProperties.Buffer queueProps = VkQueueFamilyProperties.calloc(queueCount);
        vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQueueFamilyPropertyCount, queueProps);
        
        int graphicsQueueFamilyIndex = -1;
        for (int i = 0; i < queueCount; i++) {
            if ((queueProps.get(i).queueFlags() & queueFlag) != 0){
            	graphicsQueueFamilyIndex = i;
            }
        }
        
        if (graphicsQueueFamilyIndex == -1){
        	throw new AssertionError("QueueFamily not supported");
        }
        
        memFree(pQueueFamilyPropertyCount);
        queueProps.free();
        
        return graphicsQueueFamilyIndex;
	}

	public VkQueue getVkQueueHandle() {
		return vkQueueHandle;
	}

	public void setVkQueueHandle(VkQueue vkQueueHandle) {
		this.vkQueueHandle = vkQueueHandle;
	}

	public QueueFamily getFamily() {
		return family;
	}

	public void setFamily(QueueFamily family) {
		this.family = family;
	}

}
