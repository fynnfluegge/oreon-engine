package org.oreon.core.vk.context;

import org.oreon.core.vk.framebuffer.VkFrameBufferObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VkResources {

	private VkFrameBufferObject offScreenFbo;
	private VkFrameBufferObject offScreenReflectionFbo;
	private VkFrameBufferObject transparencyFbo;

//	private HashMap<String, Descriptor> globalDescriptors;
	
}
