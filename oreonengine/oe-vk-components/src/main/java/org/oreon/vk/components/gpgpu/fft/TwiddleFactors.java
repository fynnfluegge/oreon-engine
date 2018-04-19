package org.oreon.vk.components.gpgpu.fft;

import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_STORAGE_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32B32A32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_STORAGE_BIT;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Util;
import org.oreon.core.vk.core.buffer.VkBuffer;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.descriptor.Descriptor;
import org.oreon.core.vk.core.image.VkImage;
import org.oreon.core.vk.core.image.VkImageView;
import org.oreon.core.vk.core.image.VkSampler;
import org.oreon.core.vk.core.pipeline.VkPipeline;
import org.oreon.core.vk.wrapper.VkMemoryHelper;
import org.oreon.core.vk.wrapper.image.Image2DLocal;

public class TwiddleFactors {

	private VkPipeline pipeline;
	private VkImage image;
	private VkImageView imageView;
	private VkSampler sampler;
	private Descriptor descriptor;
	private VkBuffer bitReversedIndicesBuffer;
	
	private int n;
	private int log_2_n;
	
	public TwiddleFactors(VkDevice device,
			VkPhysicalDeviceMemoryProperties memoryProperties, int n) {
		
		this.n = n;
		log_2_n = (int) (Math.log(n)/Math.log(2));
		
		image = new Image2DLocal(device, memoryProperties, log_2_n, n,
				VK_FORMAT_R32G32B32A32_SFLOAT,
				VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);
		
		imageView = new VkImageView(device,
				VK_FORMAT_R32G32B32A32_SFLOAT, image.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		sampler = new VkSampler(device);
		
		bitReversedIndicesBuffer = VkMemoryHelper.createDeviceLocalBuffer(device,
				memoryProperties,
        		VkContext.getLogicalDevice().getTransferCommandPool().getHandle(),
        		VkContext.getLogicalDevice().getTransferQueue(),
        		BufferUtil.createByteBuffer(Util.initBitReversedIndices(n)),
        		VK_BUFFER_USAGE_STORAGE_BUFFER_BIT);
	}
}
