package org.oreon.core.vk.descriptor;

import org.lwjgl.vulkan.VkDescriptorPoolSize;
import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.util.VkUtil;

import lombok.Getter;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateDescriptorPool;
import static org.lwjgl.vulkan.VK10.vkDestroyDescriptorPool;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo;

public class DescriptorPool {
	
	@Getter
	private long handle;

	public DescriptorPool(VkDevice device) {
		
		VkDescriptorPoolSize.Buffer poolSize = VkDescriptorPoolSize.calloc(1)
					.type(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
					.descriptorCount(1);
		
		VkDescriptorPoolCreateInfo createInfo = VkDescriptorPoolCreateInfo.calloc()
					.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO)
					.pPoolSizes(poolSize)
					.maxSets(1)
					.flags(0);
		LongBuffer pDescriptorPool = memAllocLong(1);
		int err = vkCreateDescriptorPool(device, createInfo, null, pDescriptorPool);
		
		handle = pDescriptorPool.get(0);
		
		poolSize.free();
		createInfo.free();
		memFree(pDescriptorPool);
		
		if (err != VK_SUCCESS) {
		    throw new AssertionError("Failed to create Descriptor pool: " + VkUtil.translateVulkanResult(err));
		}
	}
	
	public void destroy(VkDevice device){
		
		vkDestroyDescriptorPool(device, handle, null);
	}
}
