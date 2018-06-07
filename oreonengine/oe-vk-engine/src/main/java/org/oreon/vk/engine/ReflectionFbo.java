package org.oreon.vk.engine;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_MEMORY_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_LOAD_OP_CLEAR;
import static org.lwjgl.vulkan.VK10.VK_DEPENDENCY_BY_REGION_BIT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_D16_UNORM;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R16G16B16A16_UNORM;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_GENERAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SUBPASS_EXTERNAL;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.context.EngineContext;
import org.oreon.core.vk.framebuffer.FrameBufferColorAttachment;
import org.oreon.core.vk.framebuffer.FrameBufferDepthAttachment;
import org.oreon.core.vk.framebuffer.VkFrameBuffer;
import org.oreon.core.vk.framebuffer.VkFrameBufferObject;
import org.oreon.core.vk.pipeline.RenderPass;
import org.oreon.core.vk.wrapper.image.VkImageBundle;

import lombok.Getter;

@Getter
public class ReflectionFbo extends VkFrameBufferObject{

	public ReflectionFbo(VkDevice device,
			VkPhysicalDeviceMemoryProperties memoryProperties) {
		
		width = EngineContext.getConfig().getX_ScreenResolution()/1;
		height = EngineContext.getConfig().getY_ScreenResolution()/1;
		
		VkImageBundle albedoBuffer = new FrameBufferColorAttachment(device, memoryProperties, width, height,
				VK_FORMAT_R8G8B8A8_UNORM, 1);
		VkImageBundle normalBuffer = new FrameBufferColorAttachment(device, memoryProperties, width, height, 
				VK_FORMAT_R16G16B16A16_UNORM, 1);
		VkImageBundle depthBuffer = new FrameBufferDepthAttachment(device, memoryProperties, width, height,
				VK_FORMAT_D16_UNORM, 1);
		
		attachments.put(Attachment.ALBEDO, albedoBuffer);
		attachments.put(Attachment.NORMAL, normalBuffer);
		attachments.put(Attachment.DEPTH, depthBuffer);
		
		renderPass = new RenderPass(device);
		renderPass.setAttachment(VK_FORMAT_R8G8B8A8_UNORM, 1,
				VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_GENERAL,
				VK_ATTACHMENT_LOAD_OP_CLEAR);
		renderPass.setAttachment(VK_FORMAT_R16G16B16A16_UNORM, 1,
				VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_GENERAL,
				VK_ATTACHMENT_LOAD_OP_CLEAR);
		renderPass.setAttachment(VK_FORMAT_D16_UNORM, 1,
				VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL,
				VK_ATTACHMENT_LOAD_OP_CLEAR);
		renderPass.addColorAttachmentReference(0, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
		renderPass.addColorAttachmentReference(1, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
		renderPass.addDepthAttachmentReference(2, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL);
		renderPass.setSubpassDependency(VK_SUBPASS_EXTERNAL, 0,
				VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT,
	    		VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
	    		VK_ACCESS_MEMORY_READ_BIT,
	    		VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT,
	    		VK_DEPENDENCY_BY_REGION_BIT);
		renderPass.setSubpassDependency(0, VK_SUBPASS_EXTERNAL,
				VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
				VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT,
	    		VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT,
	    		VK_ACCESS_MEMORY_READ_BIT,
	    		VK_DEPENDENCY_BY_REGION_BIT);
		renderPass.createSubpass();
		renderPass.createRenderPass();
		
		depthAttachment = 1;
		colorAttachmentCount = renderPass.getAttachmentCount()-depthAttachment;
		
		LongBuffer pImageViews = memAllocLong(renderPass.getAttachmentCount());
		pImageViews.put(0, attachments.get(Attachment.ALBEDO).getImageView().getHandle());
		pImageViews.put(1, attachments.get(Attachment.NORMAL).getImageView().getHandle());
		pImageViews.put(2, attachments.get(Attachment.DEPTH).getImageView().getHandle());
		
		frameBuffer = new VkFrameBuffer(device, width, height, 1,
				pImageViews, renderPass.getHandle());
	}
	
}