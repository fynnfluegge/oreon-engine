package org.oreon.core.vk.core.descriptor;

import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkDescriptorImageInfo;
import org.lwjgl.vulkan.VkDescriptorSetAllocateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkWriteDescriptorSet;
import org.oreon.core.vk.core.util.VkUtil;

import lombok.Getter;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.vkAllocateDescriptorSets;
import static org.lwjgl.vulkan.VK10.vkUpdateDescriptorSets;

import java.nio.LongBuffer;

public class DescriptorSet {
	
	@Getter
	private long handle;

	public DescriptorSet(VkDevice device, long descriptorPool, LongBuffer layouts) {
	
		VkDescriptorSetAllocateInfo allocateInfo = VkDescriptorSetAllocateInfo.calloc()
						.sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO)
						.descriptorPool(descriptorPool)
						.pSetLayouts(layouts);
		
		LongBuffer pDescriptorSet = memAllocLong(1);
		int err = vkAllocateDescriptorSets(device, allocateInfo, pDescriptorSet);
		
		handle = pDescriptorSet.get(0);
		
		allocateInfo.free();
		memFree(pDescriptorSet);
		
		if (err != VK_SUCCESS) {
		    throw new AssertionError("Failed to create Descriptor Set: " + VkUtil.translateVulkanResult(err));
		}
	}
	
	public void updateDescriptorBuffer(VkDevice device, long buffer, long range, long offset, int binding){
		
		VkDescriptorBufferInfo.Buffer bufferInfo = VkDescriptorBufferInfo.calloc(1)
						.buffer(buffer)
						.offset(offset)
						.range(range);

		VkWriteDescriptorSet.Buffer writeDescriptor = VkWriteDescriptorSet.calloc(1)
						.sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
						.dstSet(handle)
						.dstBinding(binding)
						.dstArrayElement(0)
						.descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
						.pBufferInfo(bufferInfo);

		vkUpdateDescriptorSets(device, writeDescriptor, null);
	}
	
	public void updateDescriptorImageBuffer(VkDevice device, long imageView, long sampler, int binding){
		
		VkDescriptorImageInfo.Buffer imageInfo = VkDescriptorImageInfo.calloc(1)
						.imageLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
						.imageView(imageView)
						.sampler(sampler);
		
		VkWriteDescriptorSet.Buffer writeDescriptor = VkWriteDescriptorSet.calloc(1)
						.sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
						.dstSet(handle)
						.dstBinding(binding)
						.dstArrayElement(0)
						.descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
				 		.pImageInfo(imageInfo);
		
		vkUpdateDescriptorSets(device, writeDescriptor, null);
	}
}
