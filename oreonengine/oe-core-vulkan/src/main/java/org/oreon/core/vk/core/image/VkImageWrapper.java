package org.oreon.core.vk.core.image;

import org.lwjgl.vulkan.VkDevice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VkImageWrapper {

	private VkImage image;
	private VkImageView imageView;
	private VkSampler sampler;
	
	public VkImageWrapper(VkDevice device, VkImage image){
		
		this.image = image;
		imageView = new VkImageView(device, image.getFormat(), image.getHandle());
		sampler = new VkSampler(device);
	}
}
