package org.oreon.core.vk.wrapper.buffer;

import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;

import java.nio.ByteBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkQueue;
import org.oreon.core.model.Mesh;
import org.oreon.core.model.Vertex.VertexLayout;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.vk.command.CommandPool;
import org.oreon.core.vk.memory.VkBuffer;

import lombok.Getter;

@Getter
public class VkMeshBuffer {
	
	private VkBuffer vertexBuffer;
	private VkBuffer indexBuffer;
	private int indexCount;
	
	public VkMeshBuffer(VkDevice device, VkPhysicalDeviceMemoryProperties memoryProperties,
			CommandPool commandPool, VkQueue queue, Mesh mesh, VertexLayout vertexLayout) {
		
		ByteBuffer vertexByteBuffer = BufferUtil.createByteBuffer(mesh.getVertices(), vertexLayout);
		ByteBuffer indexByteBuffer = BufferUtil.createByteBuffer(mesh.getIndices());
		indexCount = mesh.getIndices().length;
		
		vertexBuffer = VkBufferHelper.createDeviceLocalBuffer(
				device, memoryProperties,
				commandPool.getHandle(), queue,
				vertexByteBuffer, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);

        indexBuffer = VkBufferHelper.createDeviceLocalBuffer(
        		device, memoryProperties,
				commandPool.getHandle(), queue,
        		indexByteBuffer, VK_BUFFER_USAGE_INDEX_BUFFER_BIT);
	}

}
