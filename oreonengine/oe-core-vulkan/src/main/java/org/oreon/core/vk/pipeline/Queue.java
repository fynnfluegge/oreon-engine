package org.oreon.core.vk.pipeline;

import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceQueueFamilyProperties;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.oreon.core.vk.util.VKUtil;

public class Queue {
	
	private int familyIndex;
	private int count;
	private VkQueue vkQueueHandle;

	public void createGraphicsQueue(){
		
	}
	
	public void createComputeQueue(){
		
	}
	
	public void createTransferQueue(){
		
	}
	
	public void createSparseBindingQueue(){
		
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
	
	public int getFamilyIndex() {
		return familyIndex;
	}

	public void setFamilyIndex(int familyIndex) {
		this.familyIndex = familyIndex;
	}

	public VkQueue getVkQueueHandle() {
		return vkQueueHandle;
	}

	public void setVkQueueHandle(VkQueue vkQueueHandle) {
		this.vkQueueHandle = vkQueueHandle;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
