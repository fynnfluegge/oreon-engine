package org.oreon.vk.engine;

import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_MEMORY_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_DEPENDENCY_BY_REGION_BIT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SUBPASS_EXTERNAL;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.context.EngineContext;
import org.oreon.core.vk.core.framebuffer.FrameBufferObject;
import org.oreon.core.vk.core.framebuffer.VkFrameBuffer;
import org.oreon.core.vk.core.image.VkImage;
import org.oreon.core.vk.core.image.VkImageView;
import org.oreon.core.vk.core.pipeline.RenderPass;

import lombok.Getter;

@Getter
public class OffScreenFbo extends FrameBufferObject{

	private VkFrameBuffer frameBuffer;
	private RenderPass renderPass;
	private VkImage image; 
	private VkImageView imageView;
	private int width;
	private int height;
	
	private GBuffer gbuffer;
	
	public OffScreenFbo(VkDevice device, VkPhysicalDeviceMemoryProperties memoryProperties) {
		
		int imageFormat = VK_FORMAT_B8G8R8A8_UNORM;
		this.width = EngineContext.getConfig().getDisplayWidth();
		this.height = EngineContext.getConfig().getDisplayHeight();
		
		image = new VkImage(device, width, height, 1, imageFormat, 
				VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_SAMPLED_BIT);
		image.allocate(memoryProperties,
 			   VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
		image.bindImageMemory();
		
		imageView = new VkImageView(device, imageFormat, image.getHandle());
		
		renderPass = new RenderPass(device);
		renderPass.setAttachmentDescription(imageFormat,
					VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
		renderPass.setAttachmentReferences();
		renderPass.setSubpassDependency(VK_SUBPASS_EXTERNAL, 0, VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT,
	    		VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT, VK_ACCESS_MEMORY_READ_BIT,
	    		VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT,
	    		VK_DEPENDENCY_BY_REGION_BIT);
		renderPass.setSubpass();
		renderPass.createRenderPass();
		
		frameBuffer = new VkFrameBuffer(device, width, height, 1,
				imageView.getHandle(), renderPass.getHandle());
	}
	
}
