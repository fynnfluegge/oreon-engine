package org.oreon.core.vk.command;

import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkAllocateCommandBuffers;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.util.VKUtil;

public class CommandBuffers {

	private VkCommandBuffer[] commandBuffers;
	
	public CommandBuffers(VkDevice device, long commandPool, int count) {
		
		VkCommandBufferAllocateInfo cmdBufAllocateInfo = VkCommandBufferAllocateInfo.calloc()
	                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
	                .commandPool(commandPool)
	                .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
	                .commandBufferCount(count);
		 
		PointerBuffer pCommandBuffer = memAllocPointer(count);
		int err = vkAllocateCommandBuffers(device, cmdBufAllocateInfo, pCommandBuffer);
		
		if (err != VK_SUCCESS) {
		    throw new AssertionError("Failed to allocate command buffer: " + VKUtil.translateVulkanResult(err));
		}
		
		for (int i = 0; i < count; i++) {
			commandBuffers[i] = new VkCommandBuffer(pCommandBuffer.get(i), device);
		}
		
		memFree(pCommandBuffer);
		cmdBufAllocateInfo.free();
	}
	
	public void beginCommandBuffer(){
		
	}

	public VkCommandBuffer[] getCommandBuffers() {
		return commandBuffers;
	}
	
}
