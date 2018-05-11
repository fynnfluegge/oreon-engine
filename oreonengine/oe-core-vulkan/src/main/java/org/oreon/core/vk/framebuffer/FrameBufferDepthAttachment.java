package org.oreon.core.vk.framebuffer;

import static org.lwjgl.vulkan.VK10.VK_FORMAT_D32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_DEPTH_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.vk.image.VkImage;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.core.vk.wrapper.image.Image2DLocal;

import lombok.Getter;

@Getter
public class FrameBufferDepthAttachment {

	private VkImage image;
	private VkImageView imageView;
	
	public FrameBufferDepthAttachment(VkDevice device, VkPhysicalDeviceMemoryProperties memoryProperties,
			int width, int height){
		
		image = new Image2DLocal(device, memoryProperties, width, height, VK_FORMAT_D32_SFLOAT,
				VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT | VK_IMAGE_USAGE_SAMPLED_BIT);
		imageView = new VkImageView(device, image.getFormat(), image.getHandle(),
				VK_IMAGE_ASPECT_DEPTH_BIT);
	}
}
