package org.oreon.core.vk.wrapper.descriptor;

import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_ALL_GRAPHICS;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.descriptor.DescriptorKeys.DescriptorPoolType;
import org.oreon.core.vk.core.descriptor.DescriptorSet;
import org.oreon.core.vk.core.descriptor.DescriptorSetLayout;

public class CameraDescriptor extends VkDescriptor{
	
	public CameraDescriptor(VkDevice device, long buffer) {
		
	    pool = VkContext.getEnvironment().getDescriptorPool(DescriptorPoolType.UNIFORM_BUFFER).getHandle();
	    
	    DescriptorSetLayout descriptorLayout = new DescriptorSetLayout(device, 1);
	    descriptorLayout.addLayoutBinding(0,VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,VK_SHADER_STAGE_ALL_GRAPHICS);
	    descriptorLayout.create();
	    layout = descriptorLayout;
		
	    DescriptorSet descriptorSet = new DescriptorSet(device, 
	    												pool,
	    												descriptorLayout.getHandle());
	    descriptorSet.updateDescriptorBuffer(buffer, 4 * 16, 0, 0);
	    set = descriptorSet;
	}

}
