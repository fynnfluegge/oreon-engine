package org.oreon.core.vk.wrapper.buffer;

import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;

import java.nio.ByteBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkQueue;
import org.oreon.core.vk.buffer.VkBuffer;
import org.oreon.core.vk.wrapper.command.BufferCopyCmdBuffer;

public class VkBufferHelper {
	
	public static VkBuffer createDeviceLocalBuffer(VkDevice device,
			VkPhysicalDeviceMemoryProperties memoryProperties,
			long commandPool, VkQueue queue, ByteBuffer dataBuffer, int usage){

		StagingBuffer stagingBuffer = new StagingBuffer(device, memoryProperties, dataBuffer);
		LocalBuffer deviceLocalBuffer = new LocalBuffer(device, memoryProperties,
							dataBuffer.limit(), usage);
		
		BufferCopyCmdBuffer bufferCopyCommand = new BufferCopyCmdBuffer(device, commandPool);
		bufferCopyCommand.record(stagingBuffer.getHandle(),
				deviceLocalBuffer.getHandle(), 0, 0, dataBuffer.limit());
		bufferCopyCommand.submit(queue);
		
		vkQueueWaitIdle(queue);
		
		bufferCopyCommand.destroy();
		stagingBuffer.destroy();
		
		return deviceLocalBuffer;
	}

}
