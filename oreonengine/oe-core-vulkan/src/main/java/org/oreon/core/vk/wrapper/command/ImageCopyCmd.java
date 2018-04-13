package org.oreon.core.vk.wrapper.command;

import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueue;
import org.oreon.core.vk.core.command.CommandBuffer;
import org.oreon.core.vk.core.command.SubmitInfo;

public class ImageCopyCmd extends CommandBuffer{
	
	public ImageCopyCmd(VkDevice device, long commandPool) {
		super(device, commandPool);
	}
	
	public void record(long stagingBuffer, long image){
		
		beginRecord(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
	    recordCopyBufferToImageCmd(stagingBuffer, image, 512, 512, 1);
	    finishRecord();
	}
	
	public void submit(VkQueue queue){

		SubmitInfo submitInfo = new SubmitInfo(getPHandle());
		submitInfo.submit(queue);
	}

}
