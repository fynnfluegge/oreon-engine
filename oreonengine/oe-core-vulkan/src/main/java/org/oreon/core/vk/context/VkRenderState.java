package org.oreon.core.vk.context;

import org.oreon.core.context.RenderState;
import org.oreon.core.vk.framebuffer.FrameBufferObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VkRenderState extends RenderState{

	private FrameBufferObject offScreenFbo;
	private FrameBufferObject offScreenReflectionFbo;

}
