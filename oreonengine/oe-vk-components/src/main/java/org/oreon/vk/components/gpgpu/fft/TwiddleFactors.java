package org.oreon.vk.components.gpgpu.fft;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_STORAGE_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32B32A32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_GENERAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_STORAGE_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_COMPUTE_BIT;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Util;
import org.oreon.core.vk.core.buffer.VkBuffer;
import org.oreon.core.vk.core.command.CommandBuffer;
import org.oreon.core.vk.core.command.SubmitInfo;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.descriptor.Descriptor;
import org.oreon.core.vk.core.descriptor.DescriptorSet;
import org.oreon.core.vk.core.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.core.image.VkImage;
import org.oreon.core.vk.core.image.VkImageView;
import org.oreon.core.vk.core.pipeline.ShaderModule;
import org.oreon.core.vk.core.pipeline.VkPipeline;
import org.oreon.core.vk.core.synchronization.Fence;
import org.oreon.core.vk.core.util.VkUtil;
import org.oreon.core.vk.wrapper.buffer.VkBufferHelper;
import org.oreon.core.vk.wrapper.command.ComputeCmdBuffer;
import org.oreon.core.vk.wrapper.image.Image2DLocal;

import lombok.Getter;

public class TwiddleFactors {

	@Getter
	private VkImageView imageView;
	
	private VkImage image;
	
	private class TwiddleDescriptor extends Descriptor{
		
		public TwiddleDescriptor(VkDevice device, VkImageView imageView, VkBuffer buffer, int n) {
		
			layout = new DescriptorSetLayout(device, 2);
		    layout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
		    		VK_SHADER_STAGE_COMPUTE_BIT);
		    layout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_STORAGE_BUFFER,
		    		VK_SHADER_STAGE_COMPUTE_BIT);
		    layout.create();
		    
		    set = new DescriptorSet(device,
		    		VkContext.getDescriptorPoolManager()
		    			.getDescriptorPool("POOL_1").getHandle(),
		    		layout.getHandlePointer());
		    set.updateDescriptorImageBuffer(imageView.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    set.updateDescriptorBuffer(buffer.getHandle(), Integer.BYTES * n, 0, 1, VK_DESCRIPTOR_TYPE_STORAGE_BUFFER);
		}
	}
	
	public TwiddleFactors(VkDevice device,
			VkPhysicalDeviceMemoryProperties memoryProperties, int n) {
		
		int log_2_n = (int) (Math.log(n)/Math.log(2));
		
		image = new Image2DLocal(device, memoryProperties, log_2_n, n,
				VK_FORMAT_R32G32B32A32_SFLOAT,
				VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);
		
		imageView = new VkImageView(device,
				VK_FORMAT_R32G32B32A32_SFLOAT, image.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		VkBuffer bitReversedIndicesBuffer = VkBufferHelper.createDeviceLocalBuffer(device,
				memoryProperties,
        		VkContext.getLogicalDevice().getTransferCommandPool().getHandle(),
        		VkContext.getLogicalDevice().getTransferQueue(),
        		BufferUtil.createByteBuffer(Util.initBitReversedIndices(n)),
        		VK_BUFFER_USAGE_STORAGE_BUFFER_BIT);
		
		ByteBuffer pushConstants = memAlloc(Integer.BYTES * 1);
		IntBuffer intBuffer = pushConstants.asIntBuffer();
		intBuffer.put(n);
		
		Descriptor descriptor = new TwiddleDescriptor(device, imageView, bitReversedIndicesBuffer, n);
		
		ShaderModule computeShader = new ShaderModule(device,
				"fft/TwiddleFactors.comp.spv", VK_SHADER_STAGE_COMPUTE_BIT);
		
		VkPipeline pipeline = new VkPipeline(device);
		pipeline.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, Integer.BYTES * 1);
		pipeline.setLayout(descriptor.getLayout().getHandlePointer());
		pipeline.createComputePipeline(computeShader);
		
		CommandBuffer commandBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				pipeline.getHandle(), pipeline.getLayoutHandle(),
				VkUtil.createLongArray(descriptor.getSet()),
				log_2_n, n/16, 1,
				pushConstants, VK_SHADER_STAGE_COMPUTE_BIT);
		
		Fence fence = new Fence(device);
		
		SubmitInfo submitInfo = new SubmitInfo();
		submitInfo.setCommandBuffers(commandBuffer.getHandlePointer());
		submitInfo.setFence(fence);
		submitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());
		
		fence.waitForFence();
		
		pipeline.destroy();
		commandBuffer.destroy();
		fence.destroy();
		descriptor.destroy();
		bitReversedIndicesBuffer.destroy();
		memFree(pushConstants);
	}
	
	public void destroy(){
		
		image.destroy();
		imageView.destroy();
	}
	
}
