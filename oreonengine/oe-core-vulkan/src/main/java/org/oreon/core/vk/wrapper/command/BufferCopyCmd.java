package org.oreon.core.vk.wrapper.command;

import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueue;
import org.oreon.core.vk.core.command.CommandBuffer;
import org.oreon.core.vk.core.command.SubmitInfo;

public class BufferCopyCmd extends CommandBuffer{
	
	public BufferCopyCmd(VkDevice device, long commandPool) {
		super(device, commandPool);
	}

	public void record(long srcBuffer, long dstBuffer, long srcOffset, long dstOffset, long size){
		
		beginRecord(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
		recordCopyBufferCmd(srcBuffer, dstBuffer, srcOffset, dstOffset, size);
		finishRecord();
	}
	
	public void submit(VkQueue queue){
		
		SubmitInfo submitInfo = new SubmitInfo(getPHandle());
		submitInfo.submit(queue);
	}
	
}
