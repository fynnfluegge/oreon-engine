package org.oreon.core.vk.device;

import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateDevice;
import static org.lwjgl.vulkan.VK10.vkGetDeviceQueue;
import static org.lwjgl.vulkan.VK10.vkQueueSubmit;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.oreon.core.vk.util.VKUtil;

public class LogicalDevice {

	private VkDevice handle;
	private VkQueue graphicsAndPresentationQueue;
	private VkQueue computeQueue;
	private VkQueue transferQueue;
	
	public void createGraphicsAndPresentationDevice(PhysicalDevice physicalDevice,
			 										float priority,
			 										PointerBuffer ppEnabledLayerNames){
		
		FloatBuffer pQueuePriorities = memAllocFloat(1).put(priority);
        pQueuePriorities.flip();
        
        int graphicsAndPresentationQueueFamilyIndex = physicalDevice.getQueueFamilies().getGraphicsAndPresentationQueueFamily().getIndex();
        
        VkDeviceQueueCreateInfo.Buffer queueCreateInfo = VkDeviceQueueCreateInfo.calloc(1)
                .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                .queueFamilyIndex(graphicsAndPresentationQueueFamilyIndex)
                .pQueuePriorities(pQueuePriorities);

        PointerBuffer extensions = memAllocPointer(1);
        ByteBuffer VK_KHR_SWAPCHAIN_EXTENSION = memUTF8(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
        extensions.put(VK_KHR_SWAPCHAIN_EXTENSION);
        extensions.flip();
        
        physicalDevice.checkDeviceExtensionsSupport(extensions);
        
        VkDeviceCreateInfo deviceCreateInfo = VkDeviceCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                .pNext(0)
                .pQueueCreateInfos(queueCreateInfo)
                .ppEnabledExtensionNames(extensions)
                .ppEnabledLayerNames(ppEnabledLayerNames);

        PointerBuffer pDevice = memAllocPointer(1);
        int err = vkCreateDevice(physicalDevice.getHandle(), deviceCreateInfo, null, pDevice);
       
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create device: " + VKUtil.translateVulkanResult(err));
        }
        
        handle = new VkDevice(pDevice.get(0), physicalDevice.getHandle(), deviceCreateInfo);
        
        graphicsAndPresentationQueue = createDeviceQueue(graphicsAndPresentationQueueFamilyIndex);
        
        deviceCreateInfo.free();
        memFree(pDevice);
        memFree(pQueuePriorities);
        memFree(VK_KHR_SWAPCHAIN_EXTENSION);
        memFree(extensions);
	}
	
	public void createDeviceFullQueueSupport(){
		
	}
	
	public void createGraphicsDevice(){
		
	}
	
	public void createComputeDevice(){
		
	}
	
	public void createComputeAndPresentationDevice(){
		
	}
	
	public void createTransferDevice(){
		
	}
	
	private VkQueue createDeviceQueue(int queueFamilyIndex) {
		
        PointerBuffer pQueue = memAllocPointer(1);
        vkGetDeviceQueue(handle, queueFamilyIndex, 0, pQueue);
        long queue = pQueue.get(0);
        memFree(pQueue);
        return new VkQueue(queue, handle);
    }
	
	public VkDevice getHandle() {
		return handle;
	}

	public VkQueue getGraphicsAndPresentationQueue() {
		return graphicsAndPresentationQueue;
	}

	public VkQueue getComputeQueue() {
		return computeQueue;
	}

	public VkQueue getTransferQueue() {
		return transferQueue;
	}

}
