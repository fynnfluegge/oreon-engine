package org.oreon.core.vk.wrapper.command;

import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_LEVEL_SECONDARY;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_BIND_POINT_GRAPHICS;

import java.nio.ByteBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.core.command.CommandBuffer;

public class SecondaryDrawCmdBuffer extends CommandBuffer{

	public SecondaryDrawCmdBuffer(VkDevice device, long commandPool,
			long pipeline, long pipelineLayout, long framebuffer,
			long renderpass, int subpass, long[] descriptorSets,
			long vertexBuffer, long indexBuffer, int indexCount) {
	
		super(device, commandPool, VK_COMMAND_BUFFER_LEVEL_SECONDARY);
		
		record(pipeline, pipelineLayout, framebuffer,
				renderpass, subpass, descriptorSets,
				vertexBuffer, indexBuffer, indexCount,
				null, 0);
	}
	
	public SecondaryDrawCmdBuffer(VkDevice device, long commandPool,
			long pipeline, long pipelineLayout, long framebuffer,
			long renderpass, int subpass, long[] descriptorSets,
			long vertexBuffer, long indexBuffer, int indexCount,
			ByteBuffer pushConstantsData, int pushConstantsStageFlags) {
	
		super(device, commandPool, VK_COMMAND_BUFFER_LEVEL_SECONDARY);
		
		record(pipeline, pipelineLayout, framebuffer,
				renderpass, subpass, descriptorSets,
				vertexBuffer, indexBuffer, indexCount,
				pushConstantsData, pushConstantsStageFlags);
	}
	
	private void record(long pipeline, long pipelineLayout, long framebuffer,
			long renderpass, int subpass, long[] descriptorSets, long vertexBuffer, long indexBuffer, int indexCount,
			ByteBuffer pushConstantsData, int pushConstantsStageFlags){
		
		beginRecordSecondary(VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT,
				framebuffer, renderpass, subpass);
		
		if (pushConstantsData != null){
			pushConstantsCmd(pipelineLayout, pushConstantsStageFlags, pushConstantsData);
		}
		bindPipelineCmd(pipeline, VK_PIPELINE_BIND_POINT_GRAPHICS);
		bindVertexInputCmd(vertexBuffer, indexBuffer);
		bindDescriptorSetsCmd(pipelineLayout, descriptorSets, VK_PIPELINE_BIND_POINT_GRAPHICS);
		drawIndexedCmd(indexCount);
	    finishRecord();
	}

}
