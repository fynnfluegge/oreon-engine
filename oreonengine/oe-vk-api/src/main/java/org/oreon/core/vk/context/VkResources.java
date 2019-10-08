package org.oreon.core.vk.context;

import java.util.HashMap;
import java.util.Map;

import org.oreon.core.vk.framebuffer.VkFrameBufferObject;
import org.oreon.core.vk.wrapper.descriptor.VkDescriptor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VkResources {

	private VkFrameBufferObject offScreenFbo;
	private VkFrameBufferObject offScreenReflectionFbo;
	private VkFrameBufferObject transparencyFbo;

	private Map<VkDescriptorName, VkDescriptor> descriptors = new HashMap<VkDescriptorName, VkDescriptor>();
	
	public enum VkDescriptorName{
		CAMERA,
		DIRECTIONAL_LIGHT
	}
}
