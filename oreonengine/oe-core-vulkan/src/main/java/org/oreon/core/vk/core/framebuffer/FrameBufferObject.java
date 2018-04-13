package org.oreon.core.vk.core.framebuffer;

import org.oreon.core.vk.core.image.VkImage;
import org.oreon.core.vk.core.image.VkImageView;
import org.oreon.core.vk.core.pipeline.RenderPass;

import lombok.Getter;

@Getter
public abstract class FrameBufferObject {
	
	protected VkFrameBuffer frameBuffer;
	protected RenderPass renderPass;
	protected VkImage image; 
	protected VkImageView imageView;
	protected int width;
	protected int height;

}
