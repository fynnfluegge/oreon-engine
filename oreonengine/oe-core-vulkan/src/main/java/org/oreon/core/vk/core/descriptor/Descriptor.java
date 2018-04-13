package org.oreon.core.vk.core.descriptor;

import lombok.Getter;

@Getter
public abstract class Descriptor {

	protected DescriptorSet set;
	protected DescriptorSetLayout layout;
	
	public void destroy(){
		set.destroy();
		layout.destroy();
	}
}
