package org.oreon.core.vk.wrapper.command;

import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_BIND_POINT_GRAPHICS;

import java.nio.ByteBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.core.command.CommandBuffer;

public class DrawCmdBuffer extends CommandBuffer{

	public DrawCmdBuffer(VkDevice device, long commandPool,
			long pipeline, long pipelineLayout, long renderPass,
			long frameBuffer, int width, int height, int attachmentCount,
			long[] descriptorSets, long vertexBuffer, long indexBuffer, int indexCount) {
	
		super(device, commandPool);
		
		buildCommandBuffer(pipeline, pipelineLayout, renderPass, frameBuffer,
				width, height, attachmentCount, descriptorSets,
				vertexBuffer, indexBuffer, indexCount,
				null, 0);
	}
	
	public DrawCmdBuffer(VkDevice device, long commandPool,
			long pipeline, long pipelineLayout, long renderPass,
			long frameBuffer, int width, int height, int attachmentCount,
			long[] descriptorSets, long vertexBuffer, long indexBuffer, int indexCount,
			ByteBuffer pushConstantsData, int pushConstantsStageFlags) {
	
		super(device, commandPool);
		
		buildCommandBuffer(pipeline, pipelineLayout, renderPass, frameBuffer,
				width, height, attachmentCount, descriptorSets,
				vertexBuffer, indexBuffer, indexCount,
				pushConstantsData, pushConstantsStageFlags);
	}
	
	private void buildCommandBuffer(long pipeline, long pipelineLayout, long renderPass,
			long frameBuffer, int width, int height, int attachmentCount,
			long[] descriptorSets, long vertexBuffer, long indexBuffer, int indexCount,
			ByteBuffer pushConstantsData, int pushConstantsStageFlags){
		
		beginRecord(VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT);
		beginRenderPassCmd(renderPass, frameBuffer, width, height, attachmentCount);
		if (pushConstantsData != null){
			pushConstantsCmd(pipelineLayout, pushConstantsStageFlags, pushConstantsData);
		}
		bindPipelineCmd(pipeline, VK_PIPELINE_BIND_POINT_GRAPHICS);
		bindVertexInputCmd(vertexBuffer, indexBuffer);
		bindDescriptorSetsCmd(pipelineLayout, descriptorSets, VK_PIPELINE_BIND_POINT_GRAPHICS);
		drawIndexedCmd(indexCount);
		endRenderPassCmd();
	    finishRecord();
	}
}
