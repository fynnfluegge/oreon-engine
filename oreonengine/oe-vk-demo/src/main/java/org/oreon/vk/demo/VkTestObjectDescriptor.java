package org.oreon.vk.demo;

import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.descriptor.Descriptor;
import org.oreon.core.vk.core.descriptor.DescriptorKeys.DescriptorPoolType;
import org.oreon.core.vk.core.descriptor.DescriptorSet;
import org.oreon.core.vk.core.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.core.image.VkSampler;

public class VkTestObjectDescriptor extends Descriptor{
	
	private VkSampler sampler;

	public VkTestObjectDescriptor(VkDevice device, long imageView) {
	
		layout = new DescriptorSetLayout(device,1);
	    layout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    						VK_SHADER_STAGE_FRAGMENT_BIT);
	    layout.create();
	    
	    sampler = new VkSampler(device);
	    
	    set = new DescriptorSet(device,
	    		VkContext.getEnvironment().getDescriptorPool(DescriptorPoolType.COMBINED_IMAGE_SAMPLER).getHandle(),
	    		layout.getHandle());
	    set.updateDescriptorImageBuffer(imageView, sampler.getHandle(), 0);
	}
}
