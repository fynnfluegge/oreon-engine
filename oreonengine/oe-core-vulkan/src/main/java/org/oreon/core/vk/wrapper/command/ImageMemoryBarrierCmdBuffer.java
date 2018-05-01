package org.oreon.core.vk.wrapper.command;

import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueue;
import org.oreon.core.vk.core.command.CommandBuffer;
import org.oreon.core.vk.core.command.SubmitInfo;

public class ImageMemoryBarrierCmdBuffer extends CommandBuffer{
	
	public ImageMemoryBarrierCmdBuffer(VkDevice device, long commandPool) {
		super(device, commandPool);
	}

	public void record(long image, int oldLayout, int newLayout, int dstStageMask){
		
		beginRecord(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
		recordImageMemoryBarrierCmd(image, oldLayout, newLayout, dstStageMask);
		finishRecord();
	}
	
	public void submit(VkQueue queue){
		
		SubmitInfo submitInfo = new SubmitInfo(getHandlePointer());
		submitInfo.submit(queue);
	}

}
