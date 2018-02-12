package org.oreon.core.vk.synchronization;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateSemaphore;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;
import org.oreon.core.vk.util.VKUtil;

public class Semaphore {
	
	private long handle;

	public Semaphore(VkDevice device) {
		
		VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO)
                .pNext(0)
                .flags(0);
		
		LongBuffer pSemaphore = memAllocLong(1);
		
		int err = vkCreateSemaphore(device, semaphoreCreateInfo, null, pSemaphore);
		if (err != VK_SUCCESS) {
			throw new AssertionError("Failed to create semaphore: " + VKUtil.translateVulkanResult(err));
		}
		
		handle = pSemaphore.get(0);
		
		semaphoreCreateInfo.free();
		memFree(pSemaphore);
	}
	
	public void destroy(){
		
	}

	public long getHandle() {
		return handle;
	}

}
