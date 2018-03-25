package org.oreon.core.vk.command;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_BIND_POINT_GRAPHICS;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_FAMILY_IGNORED;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUBPASS_CONTENTS_INLINE;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.VK_INDEX_TYPE_UINT32;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_TRANSFER_WRITE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_TRANSFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_SHADER_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT;
import static org.lwjgl.vulkan.VK10.vkAllocateCommandBuffers;
import static org.lwjgl.vulkan.VK10.vkFreeCommandBuffers;
import static org.lwjgl.vulkan.VK10.vkBeginCommandBuffer;
import static org.lwjgl.vulkan.VK10.vkCmdBeginRenderPass;
import static org.lwjgl.vulkan.VK10.vkCmdBindPipeline;
import static org.lwjgl.vulkan.VK10.vkCmdBindVertexBuffers;
import static org.lwjgl.vulkan.VK10.vkCmdBindIndexBuffer;
import static org.lwjgl.vulkan.VK10.vkCmdBindDescriptorSets;
import static org.lwjgl.vulkan.VK10.vkCmdCopyBuffer;
import static org.lwjgl.vulkan.VK10.vkCmdCopyBufferToImage;
import static org.lwjgl.vulkan.VK10.vkCmdPipelineBarrier;
import static org.lwjgl.vulkan.VK10.vkCmdDraw;
import static org.lwjgl.vulkan.VK10.vkCmdDrawIndexed;
import static org.lwjgl.vulkan.VK10.vkCmdEndRenderPass;
import static org.lwjgl.vulkan.VK10.vkEndCommandBuffer;
import static org.lwjgl.vulkan.VK10.vkQueueSubmit;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkBufferCopy;
import org.lwjgl.vulkan.VkBufferImageCopy;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkExtent3D;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkImageSubresourceLayers;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.lwjgl.vulkan.VkOffset3D;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkRenderPassBeginInfo;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.oreon.core.vk.synchronization.VkSemaphore;
import org.oreon.core.vk.util.VkUtil;

import lombok.Getter;

public class CommandBuffer {

	@Getter
	private VkCommandBuffer commandBuffer;
	@Getter
	private PointerBuffer pCommandBuffer;
	
	public CommandBuffer(VkDevice device, long commandPool) {
		
		VkCommandBufferAllocateInfo cmdBufAllocateInfo = VkCommandBufferAllocateInfo.calloc()
	                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
	                .commandPool(commandPool)
	                .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
	                .commandBufferCount(1);
		 
		pCommandBuffer = memAllocPointer(1);
		int err = vkAllocateCommandBuffers(device, cmdBufAllocateInfo, pCommandBuffer);
		
		if (err != VK_SUCCESS) {
		    throw new AssertionError("Failed to allocate command buffer: " + VkUtil.translateVulkanResult(err));
		}
		
		commandBuffer = new VkCommandBuffer(pCommandBuffer.get(0), device);
		
		cmdBufAllocateInfo.free();
	}
	
	public void beginRecord(int flags){
		
		VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
                .pNext(0)
                .flags(flags);
			
		int err = vkBeginCommandBuffer(commandBuffer, beginInfo);

		if (err != VK_SUCCESS) {
			throw new AssertionError("Failed to begin record command buffer: " + VkUtil.translateVulkanResult(err));
		}
        
        beginInfo.free();
	}
	
	public void finishRecord(){
			
		int err = vkEndCommandBuffer(commandBuffer);
		
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to finish record command buffer: " + VkUtil.translateVulkanResult(err));
        }
	}
	
	public void recordRenderCmd(long pipeline,
								 long renderPass,
								 long vertexBuffer,
								 VkExtent2D extent,
								 long framebuffer){
		
		VkRenderPassBeginInfo renderPassBeginInfo = VkRenderPassBeginInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
                .pNext(0)
                .renderPass(renderPass)
                .pClearValues(VkUtil.getBlackClearValues())
                .framebuffer(framebuffer);
		
		VkRect2D renderArea = renderPassBeginInfo.renderArea();
		renderArea.offset().set(0, 0);
		renderArea.extent().set(extent.width(), extent.height());
				
		vkCmdBeginRenderPass(commandBuffer, renderPassBeginInfo, VK_SUBPASS_CONTENTS_INLINE);
		vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline);
		
		LongBuffer offsets = memAllocLong(1);
        offsets.put(0, 0L);
        LongBuffer pBuffers = memAllocLong(1);
        pBuffers.put(0, vertexBuffer);
        
        vkCmdBindVertexBuffers(commandBuffer, 0, pBuffers, offsets);
		
		vkCmdDraw(commandBuffer, 3, 1, 0, 0);
		vkCmdEndRenderPass(commandBuffer);
		
		memFree(pBuffers);
        memFree(offsets);
		renderPassBeginInfo.free();
	}
	
	public void recordIndexedRenderCmd(long pipeline,
										long pipelineLayout,
										long renderPass,
										long vertexBuffer,
										long indexBuffer,
										long[] descriptorSets,
										VkExtent2D extent,
										long framebuffer){

		VkRenderPassBeginInfo renderPassBeginInfo = VkRenderPassBeginInfo.calloc()
					.sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
					.pNext(0)
					.renderPass(renderPass)
					.pClearValues(VkUtil.getBlackClearValues());
			
		VkRect2D renderArea = renderPassBeginInfo.renderArea();
		renderArea.offset().set(0, 0);
		renderArea.extent().set(extent.width(), extent.height());
		
		renderPassBeginInfo.framebuffer(framebuffer);
		
		vkCmdBeginRenderPass(commandBuffer, renderPassBeginInfo, VK_SUBPASS_CONTENTS_INLINE);
		vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline);
		
		LongBuffer offsets = memAllocLong(1);
		offsets.put(0, 0L);
		LongBuffer pVertexBuffers = memAllocLong(1);
		pVertexBuffers.put(0, vertexBuffer);
		
		vkCmdBindVertexBuffers(commandBuffer, 0, pVertexBuffers, offsets);
		vkCmdBindIndexBuffer(commandBuffer, indexBuffer, 0, VK_INDEX_TYPE_UINT32);
		vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipelineLayout,
								0, descriptorSets, null);
		
		vkCmdDrawIndexed(commandBuffer, 6, 1, 0, 0, 0);
		vkCmdEndRenderPass(commandBuffer);
		
		memFree(pVertexBuffers);
		memFree(offsets);
		renderPassBeginInfo.free();
	}
	
	public void recordCopyBufferCmd(long srcBuffer, long dstBuffer,
								   long srcOffset, long dstOffset,
								   long size){
		
		VkBufferCopy.Buffer copyRegion = VkBufferCopy.calloc(1)
					.srcOffset(srcOffset)
					.dstOffset(dstOffset)
					.size(size);
		
		vkCmdCopyBuffer(commandBuffer, srcBuffer, dstBuffer, copyRegion);
	}
	
	public void recordCopyBufferToImageCmd(long srcBuffer, long dstImage, int width, int height, int depth){
		
		VkBufferImageCopy.Buffer copyRegion = VkBufferImageCopy.calloc(1)
					.bufferOffset(0)
					.bufferRowLength(0)
					.bufferImageHeight(0);
		
		VkImageSubresourceLayers subresource = VkImageSubresourceLayers.calloc()
					.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
					.mipLevel(0)
					.baseArrayLayer(0)
					.layerCount(1);
		
		VkExtent3D extent = VkExtent3D.calloc()
					.width(width)
					.height(height)
					.depth(depth);
		
		VkOffset3D offset = VkOffset3D.calloc()
					.x(0)
					.y(0)
					.z(0);
		
		copyRegion.imageSubresource(subresource);
		copyRegion.imageExtent(extent);
		copyRegion.imageOffset(offset);
	
		vkCmdCopyBufferToImage(commandBuffer, srcBuffer, dstImage,
							   VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, copyRegion);
	}
	
	public void recordImageLayoutTransitionCmd(long image, int oldLayout, int newLayout){

		int srcStageMask = 0;
		int dstStageMask = 0;
		int srcAccessMask = 0; 
		int dstAccessMask = 0;

		if (oldLayout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL) {
		    srcAccessMask = 0;
		    dstAccessMask = VK_ACCESS_TRANSFER_WRITE_BIT;

		    srcStageMask = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
		    dstStageMask = VK_PIPELINE_STAGE_TRANSFER_BIT;
		} else if (oldLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL && newLayout == VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) {
		    srcAccessMask = VK_ACCESS_TRANSFER_WRITE_BIT;
		    dstAccessMask = VK_ACCESS_SHADER_READ_BIT;

		    srcStageMask = VK_PIPELINE_STAGE_TRANSFER_BIT;
		    dstStageMask = VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT;
		}
		
		VkImageMemoryBarrier.Buffer barrier = VkImageMemoryBarrier.calloc(1)
				.sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
				.oldLayout(oldLayout)
				.newLayout(newLayout)
				.srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
				.dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
				.image(image)
				.srcAccessMask(srcAccessMask)
				.dstAccessMask(dstAccessMask);
		
		VkImageSubresourceRange subresourceRange = VkImageSubresourceRange.calloc()
				.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
				.baseMipLevel(0)
				.levelCount(1)
				.baseArrayLayer(0)
				.layerCount(1);
		
		barrier.subresourceRange(subresourceRange);
	
		vkCmdPipelineBarrier(commandBuffer,srcStageMask,dstStageMask,0,null,null,barrier);
	}
	
	public void submit(VkQueue queue, VkSubmitInfo submitInfo){
		
		int err = vkQueueSubmit(queue, submitInfo, VK_NULL_HANDLE);
		
		if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to submit render queue: " + VkUtil.translateVulkanResult(err));
        }
	}
	
	public void destroy(VkDevice device, long commandPool){
		
		vkFreeCommandBuffers(device, commandPool, pCommandBuffer);
	}
	
	public VkSubmitInfo createSubmitInfo(VkSemaphore waitSemaphore,
										 VkSemaphore signalSemaphore, 
										 IntBuffer pWaitDstStageMask){
		
		VkSubmitInfo submitInfo = VkSubmitInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                .pNext(0)
                .waitSemaphoreCount(waitSemaphore == null ? 0 : waitSemaphore.getPHandle().remaining())
                .pWaitSemaphores(waitSemaphore == null ? null : waitSemaphore.getPHandle())
                .pWaitDstStageMask(pWaitDstStageMask)
                .pCommandBuffers(pCommandBuffer)
                .pSignalSemaphores(signalSemaphore == null ? null : signalSemaphore.getPHandle());
		
		return submitInfo;
	}
	
}
