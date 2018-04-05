package org.oreon.core.vk.wrapper.descriptor;

import org.oreon.core.vk.core.descriptor.DescriptorSet;
import org.oreon.core.vk.core.descriptor.DescriptorSetLayout;

import lombok.Getter;

@Getter
public abstract class VkDescriptor {

	protected DescriptorSet set;
	protected DescriptorSetLayout layout;
	protected long pool;
	
	public void destroy(){
		set.destroy();
		layout.destroy();
	}
}
