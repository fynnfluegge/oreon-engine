package org.oreon.core.vk.wrapper.descriptor;

import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_ALL_GRAPHICS;

import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.descriptor.DescriptorPool;
import org.oreon.core.vk.core.descriptor.DescriptorSet;
import org.oreon.core.vk.core.descriptor.DescriptorSetLayout;

public class CameraDescriptor extends VkDescriptor{
	
	public CameraDescriptor(long buffer) {

		DescriptorPool descriptorPool = new DescriptorPool(1);
	    descriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
	    descriptorPool.create(VkContext.getLogicalDevice().getHandle());
	    pool = descriptorPool.getHandle();
	    
	    DescriptorSetLayout descriptorLayout = new DescriptorSetLayout(1);
	    descriptorLayout.addLayoutBinding(0,VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,VK_SHADER_STAGE_ALL_GRAPHICS);
	    descriptorLayout.create(VkContext.getLogicalDevice().getHandle());
	    layout = descriptorLayout.getHandle();
		
	    DescriptorSet descriptorSet = new DescriptorSet(VkContext.getLogicalDevice().getHandle(), 
	    												descriptorPool.getHandle(),
	    												descriptorLayout.getHandle());
	    descriptorSet.updateDescriptorBuffer(VkContext.getLogicalDevice().getHandle(), 
			 							  buffer, 4 * 16, 0, 0);
	    set = descriptorSet.getHandle();
	}

}
