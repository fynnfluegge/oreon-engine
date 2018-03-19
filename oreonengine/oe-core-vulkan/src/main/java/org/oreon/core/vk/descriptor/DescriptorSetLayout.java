package org.oreon.core.vk.descriptor;

import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.util.VkUtil;

import lombok.Getter;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateDescriptorSetLayout;
import static org.lwjgl.vulkan.VK10.vkDestroyDescriptorSetLayout;

import java.nio.LongBuffer;

public class DescriptorSetLayout {
	
	private VkDescriptorSetLayoutBinding.Buffer layoutBindings;
	
	@Getter
	private long handle;
	@Getter
	private LongBuffer pHandle;
	
	public DescriptorSetLayout(int bindingCount) {
		
		layoutBindings = VkDescriptorSetLayoutBinding.calloc(bindingCount);
	}

	public void createDescriptorSetLayout(VkDevice device){
		
		layoutBindings.flip();
		
		VkDescriptorSetLayoutCreateInfo layoutInfo = VkDescriptorSetLayoutCreateInfo.calloc()
					.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
					.pBindings(layoutBindings);
		
		pHandle = memAllocLong(1);
		int err = vkCreateDescriptorSetLayout(device, layoutInfo, null, pHandle);
		
		if (err != VK_SUCCESS) {
			throw new AssertionError("Failed to create DescriptorSetLayout: " + VkUtil.translateVulkanResult(err));
		}
		
		handle = pHandle.get(0);
	}
	
	public void setLayoutBinding(){
		
		VkDescriptorSetLayoutBinding layoutBinding = VkDescriptorSetLayoutBinding.calloc()
					.binding(0)
					.descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
					.descriptorCount(1)
					.stageFlags(VK_SHADER_STAGE_VERTEX_BIT)
					.pImmutableSamplers(null);
		
		layoutBindings.put(layoutBinding);
	}
	
	public void destroy(VkDevice device){
		
		vkDestroyDescriptorSetLayout(device, handle, null);
	}
}
