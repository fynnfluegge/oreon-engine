package org.oreon.core.vk.wrapper.command;

import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;
import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.oreon.core.vk.core.command.CommandBuffer;

public class ImageLayoutTransitionCmd {
	
	private CommandBuffer commandBuffer;
	private VkSubmitInfo submitInfo;
	
	public ImageLayoutTransitionCmd(VkDevice device, long commandPool) {

		commandBuffer = new CommandBuffer(device, commandPool);
		submitInfo = commandBuffer.createSubmitInfo(null, null, null);
	}
	
	public void record(long image,
					   int oldLayout,
					   int newLayout){
		
		commandBuffer.beginRecord(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
		commandBuffer.recordImageLayoutTransitionCmd(image, oldLayout, newLayout);
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
