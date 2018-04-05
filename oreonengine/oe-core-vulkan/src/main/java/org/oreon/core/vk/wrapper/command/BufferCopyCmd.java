package org.oreon.core.vk.wrapper.command;

import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;
import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.oreon.core.vk.core.command.CommandBuffer;

public class BufferCopyCmd {
	
	private CommandBuffer commandBuffer;
	private VkSubmitInfo submitInfo;
	
	public BufferCopyCmd(VkDevice device, long commandPool) {
		
		commandBuffer = new CommandBuffer(device, commandPool);
		submitInfo = commandBuffer.createSubmitInfo(null, null, null);
	}

	public void record(long srcBuffer, long dstBuffer, long srcOffset, long dstOffset, long size){
		
		commandBuffer.beginRecord(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
		commandBuffer.recordCopyBufferCmd(srcBuffer, dstBuffer, srcOffset, dstOffset, size);
		commandBuffer.finishRecord();
	}
	
	public void submit(VkQueue queue){
		
		commandBuffer.submit(queue, submitInfo);
		vkQueueWaitIdle(queue);
	}
	
	public void destroy(){
		
		commandBuffer.destroy();
	}
}
