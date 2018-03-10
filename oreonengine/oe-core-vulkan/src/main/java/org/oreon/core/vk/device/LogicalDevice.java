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
import static org.lwjgl.vulkan.VK10.vkDestroyDevice;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkQueue;
import org.oreon.core.vk.util.VKUtil;

import lombok.Getter;

public class LogicalDevice {

	private VkDevice handle;
	private VkQueue graphicsQueue;
	private VkQueue computeQueue;
	private VkQueue transferQueue;
	
	@Getter
	private int graphicsQueueFamilyIndex;
	@Getter
	private int computeQueueFamilyIndex;
	@Getter
	private int transferQueueFamilyIndex;
	
	public void createDevice(PhysicalDevice physicalDevice,
							 float priority,
							 PointerBuffer ppEnabledLayerNames){
		
		FloatBuffer pQueuePriorities = memAllocFloat(1).put(priority);
        pQueuePriorities.flip();
        
        try {
			graphicsQueueFamilyIndex = physicalDevice.getQueueFamilies().getGraphicsAndPresentationQueueFamily().getIndex();
		} catch (Exception e1) {
			throw new AssertionError("no graphics and presentation queue available on device: " + physicalDevice.getProperties().deviceNameString());
		}
        try {
			computeQueueFamilyIndex = physicalDevice.getQueueFamilies().getComputeOnlyQueueFamily().getIndex();
		} catch (Exception e) {
			System.out.println("No compute dedicated queue available on device: " + physicalDevice.getProperties().deviceNameString());
			try {
				computeQueueFamilyIndex = physicalDevice.getQueueFamilies().getComputeQueueFamily().getIndex();
			} catch (Exception e1) {
				throw new AssertionError("no compute queue available on device: " + physicalDevice.getProperties().deviceNameString());
			}
		}
        try {
			transferQueueFamilyIndex = physicalDevice.getQueueFamilies().getTransferOnlyQueueFamily().getIndex();
		} catch (Exception e) {
			System.out.println("No transfer dedicated queue available on device: " + physicalDevice.getProperties().deviceNameString());
			try {
				transferQueueFamilyIndex = physicalDevice.getQueueFamilies().getTransferQueueFamily().getIndex();
			} catch (Exception e1) {
				throw new AssertionError("no transfer queue available on device: " + physicalDevice.getProperties().deviceNameString());
			}
		}
        
        VkDeviceQueueCreateInfo.Buffer queueCreateInfo = VkDeviceQueueCreateInfo.calloc(1)
                .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                .queueFamilyIndex(graphicsQueueFamilyIndex)
                .pQueuePriorities(pQueuePriorities);

        PointerBuffer extensions = memAllocPointer(1);
        ByteBuffer VK_KHR_SWAPCHAIN_EXTENSION = memUTF8(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
        extensions.put(VK_KHR_SWAPCHAIN_EXTENSION);
        extensions.flip();
        
        physicalDevice.checkDeviceExtensionsSupport(extensions);
        
        VkDeviceCreateInfo deviceCreateInfo = VkDeviceCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                .pNext(VK_NULL_HANDLE)
                .pQueueCreateInfos(queueCreateInfo)
                .ppEnabledExtensionNames(extensions)
                .ppEnabledLayerNames(ppEnabledLayerNames);

        PointerBuffer pDevice = memAllocPointer(1);
        int err = vkCreateDevice(physicalDevice.getHandle(), deviceCreateInfo, null, pDevice);
       
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create device: " + VKUtil.translateVulkanResult(err));
        }
        
        handle = new VkDevice(pDevice.get(0), physicalDevice.getHandle(), deviceCreateInfo);
        
        graphicsQueue = createDeviceQueue(graphicsQueueFamilyIndex,0);
        if (graphicsQueueFamilyIndex == computeQueueFamilyIndex){
        	computeQueue = graphicsQueue;
        }
        if (graphicsQueueFamilyIndex == transferQueueFamilyIndex){
        	transferQueue = graphicsQueue;
        }
        
        deviceCreateInfo.free();
        memFree(pDevice);
        memFree(pQueuePriorities);
        memFree(VK_KHR_SWAPCHAIN_EXTENSION);
        memFree(extensions);
	}
	
	private VkQueue createDeviceQueue(int queueFamilyIndex, int queueIndex) {
		
        PointerBuffer pQueue = memAllocPointer(1);
        vkGetDeviceQueue(handle, queueFamilyIndex, 0, pQueue);
        long queue = pQueue.get(0);
        memFree(pQueue);
        return new VkQueue(queue, handle);
    }
	
	public void destroy(){
		
		vkDestroyDevice(handle, null);
	}
	
	public VkDevice getHandle() {
		return handle;
	}

	public VkQueue getGraphicsQueue() {
		return graphicsQueue;
	}

	public VkQueue getComputeQueue() {
		return computeQueue;
	}

	public VkQueue getTransferQueue() {
		return transferQueue;
	}

}
