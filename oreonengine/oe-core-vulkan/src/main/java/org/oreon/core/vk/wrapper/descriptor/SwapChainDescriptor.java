package org.oreon.core.vk.wrapper.descriptor;

import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_FILTER_NEAREST;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.descriptor.DescriptorSet;
import org.oreon.core.vk.core.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.core.image.VkSampler;

import lombok.Getter;

@Getter
public class SwapChainDescriptor {
	
	private VkSampler sampler;
	private DescriptorSet set;
	private DescriptorSetLayout layout;

	public SwapChainDescriptor(VkDevice device, long imageView) {
		
		layout = new DescriptorSetLayout(device,1);
	    layout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    						VK_SHADER_STAGE_FRAGMENT_BIT);
	    layout.create();
	    
	    sampler = new VkSampler(device, VK_FILTER_NEAREST);
	    
	    set = new DescriptorSet(device,
	    		 VkContext.getDescriptorPoolManager().getDescriptorPool("POOL_1").getHandle(),
	    		 layout.getHandlePointer());
	    set.updateDescriptorImageBuffer(imageView, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
	    		sampler.getHandle(), 0, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	}
	
	public void destroy(){

		set.destroy();
		layout.destroy();
		sampler.destroy();
	}
}
