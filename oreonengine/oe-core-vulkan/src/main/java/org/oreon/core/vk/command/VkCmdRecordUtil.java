package org.oreon.core.vk.command;

import static org.lwjgl.vulkan.VK10.VK_PIPELINE_BIND_POINT_COMPUTE;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_MEMORY_BARRIER;
import static org.lwjgl.vulkan.VK10.vkCmdBindDescriptorSets;
import static org.lwjgl.vulkan.VK10.vkCmdBindPipeline;
import static org.lwjgl.vulkan.VK10.vkCmdDispatch;
import static org.lwjgl.vulkan.VK10.vkCmdPipelineBarrier;
import static org.lwjgl.vulkan.VK10.vkCmdPushConstants;

import java.nio.ByteBuffer;

import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkMemoryBarrier;

public class VkCmdRecordUtil {

	public static void cmdPushConstants(VkCommandBuffer commandBuffer,
			long pipelineLayout, int stageFlags, ByteBuffer data){
		
		vkCmdPushConstants(commandBuffer,
				pipelineLayout,
				stageFlags,
				0,
				data);
	}
	
	public static void cmdBindComputePipeline(VkCommandBuffer commandBuffer, long pipeline){
		
		vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_COMPUTE, pipeline);
	}
	
	public static void cmdBindComputeDescriptorSets(VkCommandBuffer commandBuffer,
			long pipelinyLayout, long[] descriptorSets){
		
		vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_COMPUTE,
				pipelinyLayout, 0, descriptorSets, null);
	}
	
	public static void cmdDispatch(VkCommandBuffer commandBuffer,
			int groupCountX, int groupCountY, int groupCountZ){
		
		vkCmdDispatch(commandBuffer, groupCountX, groupCountY, groupCountZ);
	}
	
	public static void cmdPipelineMemoryBarrier(VkCommandBuffer commandBuffer,
			int srcAccessMask, int dstAccessMask, int srcStageMask, int dstStageMask){
		
		VkMemoryBarrier.Buffer barrier = VkMemoryBarrier.calloc(1)
				.sType(VK_STRUCTURE_TYPE_MEMORY_BARRIER)
				.srcAccessMask(srcAccessMask)
				.dstAccessMask(dstAccessMask);
		
		vkCmdPipelineBarrier(commandBuffer, srcStageMask, dstStageMask,
				0, barrier, null, null);
	}
}
