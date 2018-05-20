package org.oreon.core.vk.wrapper.image;

import org.oreon.core.vk.image.VkImage;
import org.oreon.core.vk.image.VkImageView;

import lombok.Getter;

@Getter
public abstract class VkImageBundle {

	protected VkImage image;
	protected VkImageView imageView;
}
