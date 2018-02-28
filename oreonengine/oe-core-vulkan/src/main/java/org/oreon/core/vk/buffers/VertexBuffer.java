package org.oreon.core.vk.buffers;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE;
import static org.lwjgl.vulkan.VK10.vkCreateBuffer;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.util.VKUtil;

public class VertexBuffer {

	long handle;
	
	public void create(VkDevice device, ByteBuffer vertexBuffer){
		
		VkBufferCreateInfo bufInfo = VkBufferCreateInfo.calloc()
					.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
					.pNext(0)
					.size(vertexBuffer.remaining())
					.usage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT)
					.sharingMode(VK_SHARING_MODE_EXCLUSIVE)
					.flags(0);

		LongBuffer pBuffer = memAllocLong(1);
	    int err = vkCreateBuffer(device, bufInfo, null, pBuffer);
        handle = pBuffer.get(0);
        
        memFree(pBuffer);
        bufInfo.free();
        
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create vertex buffer: " + VKUtil.translateVulkanResult(err));
        }
	}
}
