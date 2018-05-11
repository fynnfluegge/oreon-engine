package org.oreon.vk.engine;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R16G16B16A16_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_UNORM;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.vk.framebuffer.FrameBufferColorAttachment;
import org.oreon.core.vk.framebuffer.FrameBufferDepthAttachment;

import lombok.Getter;

@Getter
public class GBuffer {
	
	private FrameBufferColorAttachment albedoBuffer;
	private FrameBufferColorAttachment normalBuffer;
	private FrameBufferColorAttachment worldPositionBuffer;
	private FrameBufferColorAttachment specularEmissionWrapper;
	private FrameBufferColorAttachment lightScatteringMask;
	private FrameBufferDepthAttachment depthBuffer;
	
	public GBuffer(VkDevice device, VkPhysicalDeviceMemoryProperties memoryProperties,
			int width, int height) {
		
		albedoBuffer = new FrameBufferColorAttachment(device, memoryProperties, width, height,
				VK_FORMAT_R8G8B8A8_UNORM);
		
		normalBuffer = new FrameBufferColorAttachment(device, memoryProperties, width, height, 
				VK_FORMAT_R16G16B16A16_SFLOAT);
		
		depthBuffer = new FrameBufferDepthAttachment(device, memoryProperties, width, height);
	}
	
	public LongBuffer getpImageViews(){
		
		LongBuffer pImageViews = memAllocLong(3);
		pImageViews.put(0, albedoBuffer.getImageView().getHandle());
		pImageViews.put(1, normalBuffer.getImageView().getHandle());
		pImageViews.put(2, depthBuffer.getImageView().getHandle());
		
		return pImageViews;
	}
}
