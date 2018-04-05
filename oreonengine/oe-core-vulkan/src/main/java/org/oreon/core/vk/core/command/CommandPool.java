package org.oreon.core.vk.core.command;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateCommandPool;
import static org.lwjgl.vulkan.VK10.vkDestroyCommandPool;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.core.util.VkUtil;

import lombok.Getter;

public class CommandPool {
	
	@Getter
	private long handle;
	
	private VkDevice device;
	
	public CommandPool(VkDevice device, int queueFamilyIndex){
		
		this.device = device;
		
		VkCommandPoolCreateInfo cmdPoolInfo = VkCommandPoolCreateInfo.calloc()
	                .sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
	                .queueFamilyIndex(queueFamilyIndex)
	                .flags(0);
		
		LongBuffer pCmdPool = memAllocLong(1);
		int err = vkCreateCommandPool(device, cmdPoolInfo, null, pCmdPool);
		handle = pCmdPool.get(0);
		
		cmdPoolInfo.free();
		memFree(pCmdPool);
		
		if (err != VK_SUCCESS) {
		    throw new AssertionError("Failed to create command pool: " + VkUtil.translateVulkanResult(err));
		}
	}
	
	public void destroy(){
		
		vkDestroyCommandPool(device, handle, null);
	}

}
