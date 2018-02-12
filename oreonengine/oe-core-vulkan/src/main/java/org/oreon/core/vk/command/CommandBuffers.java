package org.oreon.core.vk.command;

import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_BIND_POINT_GRAPHICS;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUBPASS_CONTENTS_INLINE;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkAllocateCommandBuffers;
import static org.lwjgl.vulkan.VK10.vkBeginCommandBuffer;
import static org.lwjgl.vulkan.VK10.vkCmdBeginRenderPass;
import static org.lwjgl.vulkan.VK10.vkCmdBindPipeline;
import static org.lwjgl.vulkan.VK10.vkCmdDraw;
import static org.lwjgl.vulkan.VK10.vkCmdEndRenderPass;
import static org.lwjgl.vulkan.VK10.vkEndCommandBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkRenderPassBeginInfo;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.oreon.core.vk.util.VKUtil;

public class CommandBuffers {

	private VkCommandBuffer[] commandBuffers;
	private VkSubmitInfo submitInfo;
	
	public CommandBuffers(VkDevice device, long commandPool, int count) {
		
		VkCommandBufferAllocateInfo cmdBufAllocateInfo = VkCommandBufferAllocateInfo.calloc()
	                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
	                .commandPool(commandPool)
	                .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
	                .commandBufferCount(count);
		 
		PointerBuffer pCommandBuffer = memAllocPointer(count);
		int err = vkAllocateCommandBuffers(device, cmdBufAllocateInfo, pCommandBuffer);
		
		if (err != VK_SUCCESS) {
		    throw new AssertionError("Failed to allocate command buffer: " + VKUtil.translateVulkanResult(err));
		}
		
		for (int i = 0; i < count; i++) {
			commandBuffers[i] = new VkCommandBuffer(pCommandBuffer.get(i), device);
		}
		
		memFree(pCommandBuffer);
		cmdBufAllocateInfo.free();
	}
	
	public void beginRecord(){
		
		VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
                .pNext(0)
                .flags(VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT);
	
		for (VkCommandBuffer commandBuffer : commandBuffers){
			
			int err = vkBeginCommandBuffer(commandBuffer, beginInfo);
 
			if (err != VK_SUCCESS) {
				throw new AssertionError("Failed to begin record command buffer: " + VKUtil.translateVulkanResult(err));
			}
		}
        
        beginInfo.free();
	}
	
	public void finishRecord(){
		
		for (VkCommandBuffer commandBuffer : commandBuffers){
			
			int err = vkEndCommandBuffer(commandBuffer);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to finish record command buffer: " + VKUtil.translateVulkanResult(err));
            }
		}
	}
	
	public void recordRenderPass(long pipeline, long renderPass, VkExtent2D extent, long[] framebuffers){
		
		VkRenderPassBeginInfo renderPassBeginInfo = VkRenderPassBeginInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
                .pNext(0)
                .renderPass(renderPass)
                .pClearValues(VKUtil.getBlackClearValues());
		VkRect2D renderArea = renderPassBeginInfo.renderArea();
			renderArea.offset().set(0, 0);
		    renderArea.extent().set(extent.width(), extent.height());
		
		for (int i=0; i<commandBuffers.length; i++){
			
			 renderPassBeginInfo.framebuffer(framebuffers[i]);
			 
			 vkCmdBeginRenderPass(commandBuffers[i], renderPassBeginInfo, VK_SUBPASS_CONTENTS_INLINE);
			 vkCmdBindPipeline(commandBuffers[i], VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline);
			 vkCmdDraw(commandBuffers[i], 3, 1, 0, 0);
			 vkCmdEndRenderPass(commandBuffers[i]);
		}
		
		renderPassBeginInfo.free();
		renderArea.free();
	}
	
	public void createSubmitInfos(){
		
	}

	public VkCommandBuffer[] getCommandBuffers() {
		return commandBuffers;
	}

	public VkSubmitInfo getSubmitInfos() {
		return submitInfo;
	}
	
}
