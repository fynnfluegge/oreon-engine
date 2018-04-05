package org.oreon.core.vk.wrapper.buffer;

import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT;

import java.nio.ByteBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.vk.core.buffers.VkBuffer;

import lombok.Getter;

public class StagingBuffer {
	
	@Getter
	private VkBuffer buffer;
	
	public StagingBuffer(VkDevice device,
						 VkPhysicalDeviceMemoryProperties memoryProperties, 
						 ByteBuffer dataBuffer) {
		
		buffer = new VkBuffer();
	    
		buffer.create(device, dataBuffer.limit(), VK_BUFFER_USAGE_TRANSFER_SRC_BIT);
		buffer.allocateBuffer(memoryProperties,
	    				       VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
		buffer.bindBufferMemory();
		buffer.mapMemory(dataBuffer);
	}
	
	public void destroy(){
		
		buffer.destroy();
	}

}
