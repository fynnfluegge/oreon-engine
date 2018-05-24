package org.oreon.core.vk.wrapper.command;

import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueue;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.SubmitInfo;

public class ImageMemoryBarrierCmdBuffer extends CommandBuffer{
	
	public ImageMemoryBarrierCmdBuffer(VkDevice device, long commandPool) {
		super(device, commandPool, VK_COMMAND_BUFFER_LEVEL_PRIMARY);
	}

	public void record(long image, int oldLayout, int newLayout, 
			int srcAccessMask, int dstAccessMask, int srcStageMask, int dstStageMask, 
			int mipLevels){
		
		beginRecord(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
		imageLayoutTransition(image, oldLayout, newLayout, srcAccessMask, dstAccessMask, 
				srcStageMask, dstStageMask, 0, mipLevels);
		finishRecord();
	}
	
	public void submit(VkQueue queue){
		
		SubmitInfo submitInfo = new SubmitInfo(getHandlePointer());
		submitInfo.submit(queue);
	}

}
