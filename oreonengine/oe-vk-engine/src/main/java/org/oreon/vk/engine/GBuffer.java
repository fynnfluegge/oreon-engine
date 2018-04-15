package org.oreon.vk.engine;

import static org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32B32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.context.EngineContext;
import org.oreon.core.vk.core.image.VkImageWrapper;
import org.oreon.core.vk.wrapper.image.Image2DLocal;

import lombok.Getter;

@Getter
public class GBuffer {
	
	private VkImageWrapper albedoBuffer;
	private VkImageWrapper normalBuffer;
	private VkImageWrapper worldPositionBuffer;
	private VkImageWrapper specularEmissionWrapper;
	private VkImageWrapper lightScatteringMask;
	private VkImageWrapper depthBuffer;
	
	public GBuffer(VkDevice device, VkPhysicalDeviceMemoryProperties memoryProperties) {
		
		int width = EngineContext.getConfig().getDisplayWidth();
		int height = EngineContext.getConfig().getDisplayHeight();
		
		albedoBuffer = new VkImageWrapper(device,
				new Image2DLocal(device, memoryProperties, width, height, VK_FORMAT_B8G8R8A8_UNORM,
					VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_SAMPLED_BIT));
		
		normalBuffer = new VkImageWrapper(device,
				new Image2DLocal(device, memoryProperties, width, height, VK_FORMAT_R32G32B32_SFLOAT,
					VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_SAMPLED_BIT));
	}
}
