package org.oreon.core.vk.wrapper.command;

import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT;

import java.nio.ByteBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.model.Mesh;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.vk.core.buffer.VkBuffer;
import org.oreon.core.vk.core.command.CommandBuffer;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.framebuffer.FrameBufferObject;
import org.oreon.core.vk.core.pipeline.PipelineResources;
import org.oreon.core.vk.core.pipeline.VkPipeline;
import org.oreon.core.vk.wrapper.VkMemoryHelper;

public class RenderCommandBuffer extends CommandBuffer{

	public RenderCommandBuffer(VkDevice device, long commandPool,
							   VkPipeline pipeline, FrameBufferObject fbo,
							   PipelineResources resources, Mesh mesh) {
		
		super(device, commandPool);
		
		ByteBuffer vertexBuffer = BufferUtil.createByteBuffer(mesh.getVertices(), mesh.getVertexLayout());
		ByteBuffer indexBuffer = BufferUtil.createByteBuffer(mesh.getIndices());
		
		VkBuffer vertexBufferObject = VkMemoryHelper.createDeviceLocalBuffer(device,
				VkContext.getPhysicalDevice().getMemoryProperties(),
				VkContext.getLogicalDevice().getTransferCommandPool().getHandle(),
				VkContext.getLogicalDevice().getTransferQueue(),
				vertexBuffer, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);

        VkBuffer indexBufferObject = VkMemoryHelper.createDeviceLocalBuffer(device,
        		VkContext.getPhysicalDevice().getMemoryProperties(),
        		VkContext.getLogicalDevice().getTransferCommandPool().getHandle(),
        		VkContext.getLogicalDevice().getTransferQueue(),
        		indexBuffer, VK_BUFFER_USAGE_INDEX_BUFFER_BIT);
		
		beginRecord(VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT);
	    recordIndexedRenderCmd(pipeline, fbo.getRenderPass().getHandle(),
	    		vertexBufferObject.getHandle(), indexBufferObject.getHandle(), mesh.getIndices().length,
	    		resources.getDescriporSets(), fbo.getWidth(), fbo.getHeight(), fbo.getFrameBuffer().getHandle(),
	    		fbo.getAttachmentCount());
	    finishRecord();
	}

}
