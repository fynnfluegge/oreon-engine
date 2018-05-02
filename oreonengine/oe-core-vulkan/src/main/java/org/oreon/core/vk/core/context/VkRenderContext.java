package org.oreon.core.vk.core.context;

import org.oreon.core.vk.core.framebuffer.VkFrameBuffer;
import org.oreon.core.vk.core.pipeline.RenderPass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VkRenderContext {
	
	private VkFrameBuffer offScreenFrameBuffer;
	private RenderPass offScreenRenderPass;
	private int offScreenAttachmentCount;

}
