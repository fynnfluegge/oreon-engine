package org.oreon.core.vk.core.descriptor;

import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.core.util.VkUtil;

import lombok.Getter;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateDescriptorSetLayout;
import static org.lwjgl.vulkan.VK10.vkDestroyDescriptorSetLayout;

import java.nio.LongBuffer;

public class DescriptorSetLayout {
	
	@Getter
	private long handle;
	@Getter
	private LongBuffer pHandle;
	
	private VkDescriptorSetLayoutBinding.Buffer layoutBindings;
	
	public DescriptorSetLayout(int bindingCount) {
		
		layoutBindings = VkDescriptorSetLayoutBinding.calloc(bindingCount);
	}

	public void create(VkDevice device){
		
		layoutBindings.flip();
		
		VkDescriptorSetLayoutCreateInfo layoutInfo = VkDescriptorSetLayoutCreateInfo.calloc()
					.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
					.pBindings(layoutBindings);
		
		pHandle = memAllocLong(1);
		int err = vkCreateDescriptorSetLayout(device, layoutInfo, null, pHandle);
		
		handle = pHandle.get(0);
		layoutBindings.free();
		layoutInfo.free();
		
		if (err != VK_SUCCESS) {
			throw new AssertionError("Failed to create DescriptorSetLayout: " + VkUtil.translateVulkanResult(err));
		}
	}
	
	public void addLayoutBinding(int binding, int type, int stageflags){
		
		VkDescriptorSetLayoutBinding layoutBinding = VkDescriptorSetLayoutBinding.calloc()
					.binding(binding)
					.descriptorType(type)
					.descriptorCount(1)
					.stageFlags(stageflags)
					.pImmutableSamplers(null);
		
		layoutBindings.put(layoutBinding);
	}
	
	public void destroy(VkDevice device){
		
		vkDestroyDescriptorSetLayout(device, handle, null);
	}
}
