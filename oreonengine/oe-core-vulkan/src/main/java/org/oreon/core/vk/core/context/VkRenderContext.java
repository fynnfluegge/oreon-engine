package org.oreon.core.vk.core.context;

import java.util.ArrayList;
import java.util.List;

import org.oreon.core.vk.core.command.CommandBuffer;
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
	private List<CommandBuffer> offScreenSecondaryCmdBuffers;
	
	public VkRenderContext() {
		offScreenSecondaryCmdBuffers = new ArrayList<CommandBuffer>();
	}

}
