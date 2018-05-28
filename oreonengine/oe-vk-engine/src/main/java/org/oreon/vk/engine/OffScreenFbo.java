package org.oreon.vk.engine;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_MEMORY_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_LOAD_OP_CLEAR;
import static org.lwjgl.vulkan.VK10.VK_DEPENDENCY_BY_REGION_BIT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_D32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R16G16B16A16_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_GENERAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SUBPASS_EXTERNAL;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.context.EngineContext;
import org.oreon.core.target.Attachment;
import org.oreon.core.vk.framebuffer.FrameBufferColorAttachment;
import org.oreon.core.vk.framebuffer.FrameBufferDepthAttachment;
import org.oreon.core.vk.framebuffer.VkFrameBuffer;
import org.oreon.core.vk.framebuffer.VkFrameBufferObject;
import org.oreon.core.vk.pipeline.RenderPass;
import org.oreon.core.vk.wrapper.image.VkImageBundle;

import lombok.Getter;

@Getter
public class OffScreenFbo extends VkFrameBufferObject {

	public OffScreenFbo(VkDevice device, VkPhysicalDeviceMemoryProperties memoryProperties) {

		width = EngineContext.getConfig().getX_ScreenResolution();
		height = EngineContext.getConfig().getY_ScreenResolution();

		VkImageBundle albedoAttachment = new FrameBufferColorAttachment(device, memoryProperties,
				width, height, VK_FORMAT_R8G8B8A8_UNORM);

		VkImageBundle normalAttachment = new FrameBufferColorAttachment(device, memoryProperties,
				width, height, VK_FORMAT_R16G16B16A16_SFLOAT);

		VkImageBundle worldPositionAttachment = new FrameBufferColorAttachment(device, memoryProperties,
				width, height, VK_FORMAT_R16G16B16A16_SFLOAT);
		
		VkImageBundle lightScatteringMaskAttachment = new FrameBufferColorAttachment(device, memoryProperties,
				width, height, VK_FORMAT_R16G16B16A16_SFLOAT);
		
		VkImageBundle specularEmissionAttachment = new FrameBufferColorAttachment(device, memoryProperties,
				width, height, VK_FORMAT_R16G16B16A16_SFLOAT);

		VkImageBundle depthBuffer = new FrameBufferDepthAttachment(device, memoryProperties,
				width, height, VK_FORMAT_D32_SFLOAT);

		attachments.put(Attachment.ALBEDO, albedoAttachment);
		attachments.put(Attachment.POSITION, worldPositionAttachment);
		attachments.put(Attachment.NORMAL, normalAttachment);
		attachments.put(Attachment.LIGHT_SCATTERING, lightScatteringMaskAttachment);
		attachments.put(Attachment.SPECULAR_EMISSION, specularEmissionAttachment);
		attachments.put(Attachment.DEPTH, depthBuffer);

		renderPass = new RenderPass(device);
		renderPass.setAttachment(VK_FORMAT_R8G8B8A8_UNORM, VK_IMAGE_LAYOUT_UNDEFINED,
				VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, VK_ATTACHMENT_LOAD_OP_CLEAR);
		renderPass.setAttachment(VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_LAYOUT_UNDEFINED,
				VK_IMAGE_LAYOUT_GENERAL, VK_ATTACHMENT_LOAD_OP_CLEAR);
		renderPass.setAttachment(VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_LAYOUT_UNDEFINED,
				VK_IMAGE_LAYOUT_GENERAL, VK_ATTACHMENT_LOAD_OP_CLEAR);
		renderPass.setAttachment(VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_LAYOUT_UNDEFINED,
				VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, VK_ATTACHMENT_LOAD_OP_CLEAR);
		renderPass.setAttachment(VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_LAYOUT_UNDEFINED,
				VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, VK_ATTACHMENT_LOAD_OP_CLEAR);
		renderPass.setAttachment(VK_FORMAT_D32_SFLOAT, VK_IMAGE_LAYOUT_UNDEFINED,
				VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL, VK_ATTACHMENT_LOAD_OP_CLEAR);
		renderPass.addColorAttachmentReference(0, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
		renderPass.addColorAttachmentReference(1, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
		renderPass.addColorAttachmentReference(2, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
		renderPass.addColorAttachmentReference(3, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
		renderPass.addColorAttachmentReference(4, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
		renderPass.addDepthAttachmentReference(5, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL);
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

		frameBuffer = new VkFrameBuffer(device, width, height, 1, getpImageViews(), renderPass.getHandle());
	}

	public LongBuffer getpImageViews() {

		LongBuffer pImageViews = memAllocLong(renderPass.getAttachmentCount());
		pImageViews.put(0, attachments.get(Attachment.ALBEDO).getImageView().getHandle());
		pImageViews.put(1, attachments.get(Attachment.POSITION).getImageView().getHandle());
		pImageViews.put(2, attachments.get(Attachment.NORMAL).getImageView().getHandle());
		pImageViews.put(3, attachments.get(Attachment.SPECULAR_EMISSION).getImageView().getHandle());
		pImageViews.put(4, attachments.get(Attachment.LIGHT_SCATTERING).getImageView().getHandle());
		pImageViews.put(5, attachments.get(Attachment.DEPTH).getImageView().getHandle());

		return pImageViews;
	}

}
