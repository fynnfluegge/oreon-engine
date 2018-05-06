package org.oreon.core.vk.core.framebuffer;

import org.oreon.core.vk.core.pipeline.RenderPass;

import lombok.Getter;

@Getter
public abstract class FrameBufferObject {
	
	protected VkFrameBuffer frameBuffer;
	protected RenderPass renderPass;
	protected int attachmentCount;
	protected boolean depthAttachment;
	protected int width;
	protected int height;

}
