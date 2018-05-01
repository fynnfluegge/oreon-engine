package org.oreon.core.vk.wrapper.descriptor;

import org.oreon.core.vk.core.descriptor.DescriptorSet;
import org.oreon.core.vk.core.descriptor.DescriptorSetLayout;

import lombok.Getter;

@Getter
public abstract class VkDescriptor {

	protected DescriptorSet descriptorSet;
	protected DescriptorSetLayout descriptorSetLayout;
	
	public void destroy(){
		
		descriptorSet.destroy();
		descriptorSetLayout.destroy();
	}
}
