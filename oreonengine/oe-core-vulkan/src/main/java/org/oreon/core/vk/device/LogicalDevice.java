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
import static org.lwjgl.vulkan.VK10.vkDestroyDevice;
import static org.lwjgl.vulkan.VK10.vkGetDeviceQueue;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkQueue;
import org.oreon.core.vk.command.CommandPool;
import org.oreon.core.vk.util.VkUtil;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
@Getter
public class LogicalDevice {
	
	private VkDevice handle;
	private VkQueue graphicsQueue;
	private VkQueue computeQueue;
	private VkQueue transferQueue;
	
	private CommandPool graphicsCommandPool;
	private CommandPool computeCommandPool;
	private CommandPool transferCommandPool;
	
	private int graphicsQueueFamilyIndex;
	private int computeQueueFamilyIndex;
	private int transferQueueFamilyIndex;
	
	public void createDevice(PhysicalDevice physicalDevice,
							 float priority,
							 PointerBuffer ppEnabledLayerNames){
		
		FloatBuffer pQueuePriorities = memAllocFloat(1).put(priority);
        pQueuePriorities.flip();
        
        int createInfoCount = 3;
        try {
			graphicsQueueFamilyIndex = physicalDevice.getQueueFamilies().getGraphicsAndPresentationQueueFamily().getIndex();
		} catch (Exception e1) {
			throw new AssertionError("no graphics and presentation queue available on device: " + physicalDevice.getProperties().deviceNameString());
		}
        try {
			computeQueueFamilyIndex = physicalDevice.getQueueFamilies().getComputeOnlyQueueFamily().getIndex();
		} catch (Exception e) {
			log.info("No compute dedicated queue available on device: " + physicalDevice.getProperties().deviceNameString());
			createInfoCount--;
			try {
				computeQueueFamilyIndex = physicalDevice.getQueueFamilies().getComputeQueueFamily().getIndex();
			} catch (Exception e1) {
				throw new AssertionError("no compute queue available on device: " + physicalDevice.getProperties().deviceNameString());
			}
		}
        try {
			transferQueueFamilyIndex = physicalDevice.getQueueFamilies().getTransferOnlyQueueFamily().getIndex();
		} catch (Exception e) {
			log.info("No transfer dedicated queue available on device: " + physicalDevice.getProperties().deviceNameString());
			createInfoCount--;
			try {
				transferQueueFamilyIndex = physicalDevice.getQueueFamilies().getTransferQueueFamily().getIndex();
			} catch (Exception e1) {
				throw new AssertionError("no transfer queue available on device: " + physicalDevice.getProperties().deviceNameString());
			}
		}
        
        VkDeviceQueueCreateInfo.Buffer queueCreateInfos = VkDeviceQueueCreateInfo.calloc(createInfoCount);
        
        VkDeviceQueueCreateInfo.Buffer graphicsQueueCreateInfo = VkDeviceQueueCreateInfo.calloc(1)
                .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                .queueFamilyIndex(graphicsQueueFamilyIndex)
                .pQueuePriorities(pQueuePriorities);
        
        VkDeviceQueueCreateInfo.Buffer computeQueueCreateInfo = VkDeviceQueueCreateInfo.calloc(1);
        VkDeviceQueueCreateInfo.Buffer transferQueueCreateInfo = VkDeviceQueueCreateInfo.calloc(1);
        
        queueCreateInfos.put(graphicsQueueCreateInfo);
        
        if (graphicsQueueFamilyIndex != computeQueueFamilyIndex){
        	
        	computeQueueCreateInfo = VkDeviceQueueCreateInfo.calloc(1)
                    .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                    .queueFamilyIndex(computeQueueFamilyIndex)
                    .pQueuePriorities(pQueuePriorities);
        	
        	queueCreateInfos.put(computeQueueCreateInfo);
        }
        
        if (graphicsQueueFamilyIndex != transferQueueFamilyIndex){
        	
        	transferQueueCreateInfo = VkDeviceQueueCreateInfo.calloc(1)
                    .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                    .queueFamilyIndex(transferQueueFamilyIndex)
                    .pQueuePriorities(pQueuePriorities);
        	
        	queueCreateInfos.put(transferQueueCreateInfo);
        }
        
        queueCreateInfos.flip();

        PointerBuffer extensions = memAllocPointer(1);
        ByteBuffer VK_KHR_SWAPCHAIN_EXTENSION = memUTF8(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
        extensions.put(VK_KHR_SWAPCHAIN_EXTENSION);
        extensions.flip();
        
        physicalDevice.checkDeviceExtensionsSupport(extensions);
        
        VkDeviceCreateInfo deviceCreateInfo = VkDeviceCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                .pNext(VK_NULL_HANDLE)
                .pQueueCreateInfos(queueCreateInfos)
                .ppEnabledExtensionNames(extensions)
                .ppEnabledLayerNames(ppEnabledLayerNames);

        PointerBuffer pDevice = memAllocPointer(1);
        int err = vkCreateDevice(physicalDevice.getHandle(), deviceCreateInfo, null, pDevice);
       
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create device: " + VkUtil.translateVulkanResult(err));
        }
        
        handle = new VkDevice(pDevice.get(0), physicalDevice.getHandle(), deviceCreateInfo);
        
        // create Queues and CommandPools
        graphicsQueue = createDeviceQueue(graphicsQueueFamilyIndex,0);
        graphicsCommandPool = new CommandPool(handle, graphicsQueueFamilyIndex);
        
        if (graphicsQueueFamilyIndex == computeQueueFamilyIndex){
        	computeQueue = graphicsQueue;
        	computeCommandPool = graphicsCommandPool;
        }
        else{
        	computeQueue = createDeviceQueue(computeQueueFamilyIndex,0);
        	computeCommandPool = new CommandPool(handle, computeQueueFamilyIndex);
        }
        if (graphicsQueueFamilyIndex == transferQueueFamilyIndex){
        	transferQueue = graphicsQueue;
        	transferCommandPool = graphicsCommandPool;
        }
        else{
        	transferQueue = createDeviceQueue(transferQueueFamilyIndex,0);
        	transferCommandPool = new CommandPool(handle, transferQueueFamilyIndex);
        }
        
        deviceCreateInfo.free();
        queueCreateInfos.free();
        graphicsQueueCreateInfo.free();
        computeQueueCreateInfo.free();
        transferQueueCreateInfo.free();
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

		graphicsCommandPool.destroy();
		if (graphicsQueueFamilyIndex != computeQueueFamilyIndex){
			computeCommandPool.destroy();
		}
		if (graphicsQueueFamilyIndex != transferQueueFamilyIndex){
			transferCommandPool.destroy();
		}
		vkDestroyDevice(handle, null);
	}
	
}
