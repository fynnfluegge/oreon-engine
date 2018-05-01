package org.oreon.core.vk.core.descriptor;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.EXTDescriptorIndexing.VK_DESCRIPTOR_POOL_CREATE_UPDATE_AFTER_BIND_BIT_EXT;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_POOL_CREATE_FREE_DESCRIPTOR_SET_BIT;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateDescriptorPool;
import static org.lwjgl.vulkan.VK10.vkDestroyDescriptorPool;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo;
import org.lwjgl.vulkan.VkDescriptorPoolSize;
import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.core.util.VkUtil;

import lombok.Getter;

public class DescriptorPool {
	
	@Getter
	private long handle;
	
	private VkDescriptorPoolSize.Buffer poolSizes;
	private VkDevice device;

	public DescriptorPool(int poolSizeCount) {
		
		poolSizes = VkDescriptorPoolSize.calloc(poolSizeCount);
	}
	
	public void create(VkDevice device, int maxSets) {
		
		this.device = device;
		
		poolSizes.flip();
		
		VkDescriptorPoolCreateInfo createInfo = VkDescriptorPoolCreateInfo.calloc()
					.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO)
					.pPoolSizes(poolSizes)
					.maxSets(maxSets)
					.flags(VK_DESCRIPTOR_POOL_CREATE_FREE_DESCRIPTOR_SET_BIT |
							VK_DESCRIPTOR_POOL_CREATE_UPDATE_AFTER_BIND_BIT_EXT);
		LongBuffer pDescriptorPool = memAllocLong(1);
		int err = vkCreateDescriptorPool(device, createInfo, null, pDescriptorPool);
		
		handle = pDescriptorPool.get(0);
		
		poolSizes.free();
		createInfo.free();
		memFree(pDescriptorPool);
		
		if (err != VK_SUCCESS) {
		    throw new AssertionError("Failed to create Descriptor pool: " + VkUtil.translateVulkanResult(err));
		}
	}
	
	public void addPoolSize(int type, int descriptorCount){
	
		VkDescriptorPoolSize poolSize = VkDescriptorPoolSize.calloc()
					.type(type)
					.descriptorCount(descriptorCount);
		
		poolSizes.put(poolSize);
	}
	
	public void destroy(){
		
		vkDestroyDescriptorPool(device, handle, null);
	}
}
