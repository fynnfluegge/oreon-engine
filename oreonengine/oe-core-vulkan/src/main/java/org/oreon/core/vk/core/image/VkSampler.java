package org.oreon.core.vk.core.image;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_BORDER_COLOR_INT_OPAQUE_BLACK;
import static org.lwjgl.vulkan.VK10.VK_COMPARE_OP_ALWAYS;
import static org.lwjgl.vulkan.VK10.VK_FILTER_LINEAR;
import static org.lwjgl.vulkan.VK10.VK_SAMPLER_ADDRESS_MODE_REPEAT;
import static org.lwjgl.vulkan.VK10.VK_SAMPLER_MIPMAP_MODE_LINEAR;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateSampler;
import static org.lwjgl.vulkan.VK10.vkDestroySampler;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkSamplerCreateInfo;
import org.oreon.core.vk.core.util.VkUtil;

import lombok.Getter;

public class VkSampler {
	
	@Getter
	private long handle;
	
	private VkDevice device;
	
	public void create(VkDevice device){
		
		this.device = device;
		
		VkSamplerCreateInfo createInfo = VkSamplerCreateInfo.calloc()
						.sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO)
						.magFilter(VK_FILTER_LINEAR)
						.minFilter(VK_FILTER_LINEAR)
						.addressModeU(VK_SAMPLER_ADDRESS_MODE_REPEAT)
						.addressModeV(VK_SAMPLER_ADDRESS_MODE_REPEAT)
						.addressModeW(VK_SAMPLER_ADDRESS_MODE_REPEAT)
						.anisotropyEnable(false)
						.maxAnisotropy(1)
						.borderColor(VK_BORDER_COLOR_INT_OPAQUE_BLACK)
						.unnormalizedCoordinates(false)
						.compareEnable(false)
						.compareOp(VK_COMPARE_OP_ALWAYS)
						.mipmapMode(VK_SAMPLER_MIPMAP_MODE_LINEAR)
						.mipLodBias(0)
						.minLod(0)
						.maxLod(0);
		
		LongBuffer pBuffer = memAllocLong(1);
		int err = vkCreateSampler(device, createInfo,  null, pBuffer);
		handle = pBuffer.get(0);
		
		memFree(pBuffer);
        createInfo.free();
        
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create sampler: " + VkUtil.translateVulkanResult(err));
        }
	}
	
	public void destroy(){
		
		vkDestroySampler(device, handle, null);
	}

}
