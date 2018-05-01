package org.oreon.core.vk.wrapper.descriptor;

import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_ALL_GRAPHICS;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.descriptor.DescriptorSet;
import org.oreon.core.vk.core.descriptor.DescriptorSetLayout;

import lombok.Getter;

@Getter
public class CameraDescriptor {
	
	private DescriptorSet set;
	private DescriptorSetLayout layout;
	
	public CameraDescriptor(VkDevice device, long buffer) {
		
	    layout = new DescriptorSetLayout(device, 1);
	    layout.addLayoutBinding(0,VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,VK_SHADER_STAGE_ALL_GRAPHICS);
	    layout.create();
		
	    set = new DescriptorSet(device,
	    		VkContext.getDescriptorPoolManager().getDescriptorPool("POOL_1").getHandle(),
	    		layout.getHandlePointer());
	    set.updateDescriptorBuffer(buffer, Float.BYTES * 16, 0, 0, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
	}

}
