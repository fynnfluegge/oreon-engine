package org.oreon.core.vk.wrapper.command;

import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_BIND_POINT_COMPUTE;

import java.nio.ByteBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.core.command.CommandBuffer;

public class ComputeCmdBuffer extends CommandBuffer{

	public ComputeCmdBuffer(VkDevice device, long commandPool,
			long pipeline, long pipelineLayout, long[] descriptorSets,
			int groupCountX, int groupCountY, int groupCountZ) {
		
		super(device, commandPool);
		
		buildCommandBuffer(pipeline, pipelineLayout, descriptorSets,
				groupCountX, groupCountY, groupCountZ, null, 0);
	}
	
	public ComputeCmdBuffer(VkDevice device, long commandPool,
			long pipeline, long pipelineLayout, long[] descriptorSets,
			int groupCountX, int groupCountY, int groupCountZ,
			ByteBuffer pushConstantsData, int pushConstantsStageFlags) {
		
		super(device, commandPool);
		
		buildCommandBuffer(pipeline, pipelineLayout, descriptorSets,
				groupCountX, groupCountY, groupCountZ,
				pushConstantsData, pushConstantsStageFlags);
	}

	public void buildCommandBuffer(long pipeline, long pipelineLayout,
			long[] descriptorSets, int groupCountX, int groupCountY, int groupCountZ, 
			ByteBuffer pushConstantsData, int pushConstantsStageFlags){
		
		beginRecord(VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT);
		if (pushConstantsData != null){
			pushConstantsCmd(pipelineLayout, pushConstantsStageFlags, pushConstantsData);
		}
		bindPipelineCmd(pipeline, VK_PIPELINE_BIND_POINT_COMPUTE);
		bindDescriptorSetsCmd(pipelineLayout, descriptorSets, VK_PIPELINE_BIND_POINT_COMPUTE);
		dispatchCmd(groupCountX, groupCountY, groupCountZ);
	    finishRecord();
	}
	
}
