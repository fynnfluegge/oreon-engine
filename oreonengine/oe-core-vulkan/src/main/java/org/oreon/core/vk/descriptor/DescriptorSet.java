package org.oreon.core.vk.descriptor;

import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkDescriptorSetAllocateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkWriteDescriptorSet;
import org.oreon.core.vk.util.VkUtil;

import lombok.Getter;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
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
	
	public void configureWrite(VkDevice device, long buffer, long offset, long range){
		
		VkDescriptorBufferInfo.Buffer bufferInfo = VkDescriptorBufferInfo.calloc(1)
						.buffer(buffer)
						.offset(offset)
						.range(range);

		VkWriteDescriptorSet.Buffer descriptorWrite = VkWriteDescriptorSet.calloc(1)
						.sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
						.dstSet(handle)
						.dstBinding(0)
						.dstArrayElement(0)
						.descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
						.pBufferInfo(bufferInfo)
						.pImageInfo(null)
						.pTexelBufferView(null);

		vkUpdateDescriptorSets(device, descriptorWrite, null);
	}
}
