package org.oreon.core.vk.framebuffer;

import java.util.HashMap;

import org.oreon.core.target.FrameBufferObject;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.core.vk.pipeline.RenderPass;
import org.oreon.core.vk.wrapper.image.VkImageBundle;

import lombok.Getter;

@Getter
public class VkFrameBufferObject extends FrameBufferObject{
	
	protected VkFrameBuffer frameBuffer;
	protected RenderPass renderPass;
	protected HashMap<Attachment, VkImageBundle> attachments = new HashMap<>();
	
	public VkImageView getAttachmentImageView(Attachment type){
		
		return attachments.get(type).getImageView();
	}
}
