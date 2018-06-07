package org.oreon.core.vk.wrapper.descriptor;

import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_ALL_GRAPHICS;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_COMPUTE_BIT;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;

import lombok.Getter;

@Getter
public class CameraDescriptor {
	
	private DescriptorSet set;
	private DescriptorSetLayout layout;
	
	public CameraDescriptor(VkDevice device, long descriptorPool, long buffer, int bufferSize) {
		
	    layout = new DescriptorSetLayout(device, 1);
	    layout.addLayoutBinding(0,VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
	    		VK_SHADER_STAGE_ALL_GRAPHICS | VK_SHADER_STAGE_COMPUTE_BIT);
	    layout.create();
		
	    set = new DescriptorSet(device, descriptorPool, layout.getHandlePointer());
	    set.updateDescriptorBuffer(buffer, bufferSize, 0, 0, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
	}

}
