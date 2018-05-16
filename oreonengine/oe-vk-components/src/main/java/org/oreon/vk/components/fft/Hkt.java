package org.oreon.vk.components.fft;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32B32A32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_GENERAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_STORAGE_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_COMPUTE_BIT;

import java.nio.ByteBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.SubmitInfo;
import org.oreon.core.vk.context.VkContext;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.image.VkImage;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.core.vk.pipeline.ShaderModule;
import org.oreon.core.vk.pipeline.VkPipeline;
import org.oreon.core.vk.synchronization.Fence;
import org.oreon.core.vk.util.VkUtil;
import org.oreon.core.vk.wrapper.buffer.VkUniformBuffer;
import org.oreon.core.vk.wrapper.command.ComputeCmdBuffer;
import org.oreon.core.vk.wrapper.descriptor.VkDescriptor;
import org.oreon.core.vk.wrapper.image.Image2DLocal;

import lombok.Getter;

public class Hkt extends Renderable{

	@Getter
	private VkImageView dxCoefficients_imageView;
	@Getter
	private VkImageView dyCoefficients_imageView;
	@Getter
	private VkImageView dzCoefficients_imageView;
	
	@Getter
	private Fence fence;
	
	private float t = 0;
	private float t_delta = 240;
	private long systemTime = System.currentTimeMillis();
	
	private VkImage image_dxCoefficients;
	private VkImage image_dyCoefficients;
	private VkImage image_dzCoefficients;
	
	private VkPipeline pipeline;
	private VkDescriptor descriptor;
	private VkUniformBuffer buffer;
	
	private CommandBuffer commandBuffer;
	private SubmitInfo submitInfo;
	
	private class CoefficientsDescriptor extends VkDescriptor{
		
		public CoefficientsDescriptor(VkDevice device,
				VkImageView tilde_h0k, VkImageView tilde_h0minusk) {
		
			descriptorSetLayout = new DescriptorSetLayout(device, 6);
			descriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
		    		VK_SHADER_STAGE_COMPUTE_BIT);
			descriptorSetLayout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
		    		VK_SHADER_STAGE_COMPUTE_BIT);
			descriptorSetLayout.addLayoutBinding(2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
		    		VK_SHADER_STAGE_COMPUTE_BIT);
			descriptorSetLayout.addLayoutBinding(3, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
		    		VK_SHADER_STAGE_COMPUTE_BIT);
			descriptorSetLayout.addLayoutBinding(4, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
		    		VK_SHADER_STAGE_COMPUTE_BIT);
			descriptorSetLayout.addLayoutBinding(5,VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
		    		VK_SHADER_STAGE_COMPUTE_BIT);
			descriptorSetLayout.create();
		    
		    descriptorSet = new DescriptorSet(device,
		    		VkContext.getDescriptorPoolManager().getDescriptorPool("POOL_1").getHandle(),
		    		descriptorSetLayout.getHandlePointer());
		    descriptorSet.updateDescriptorImageBuffer(dyCoefficients_imageView.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    descriptorSet.updateDescriptorImageBuffer(dxCoefficients_imageView.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    descriptorSet.updateDescriptorImageBuffer(dzCoefficients_imageView.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    descriptorSet.updateDescriptorImageBuffer(tilde_h0k.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		3, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    descriptorSet.updateDescriptorImageBuffer(tilde_h0minusk.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		4, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    descriptorSet.updateDescriptorBuffer(buffer.getHandle(),
		    		Float.BYTES * 1, 0, 5, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
		}
	}
	
	public Hkt(VkDevice device,
			VkPhysicalDeviceMemoryProperties memoryProperties, int N, int L,
			VkImageView tilde_h0k, VkImageView tilde_h0minusk) {
		
		image_dxCoefficients = new Image2DLocal(device, memoryProperties, N, N,
				VK_FORMAT_R32G32B32A32_SFLOAT,
				VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);
		
		dxCoefficients_imageView = new VkImageView(device,
				VK_FORMAT_R32G32B32A32_SFLOAT, image_dxCoefficients.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		image_dyCoefficients = new Image2DLocal(device, memoryProperties, N, N,
				VK_FORMAT_R32G32B32A32_SFLOAT,
				VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);
		
		dyCoefficients_imageView = new VkImageView(device,
				VK_FORMAT_R32G32B32A32_SFLOAT, image_dyCoefficients.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		image_dzCoefficients = new Image2DLocal(device, memoryProperties, N, N,
				VK_FORMAT_R32G32B32A32_SFLOAT,
				VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);
		
		dzCoefficients_imageView = new VkImageView(device,
				VK_FORMAT_R32G32B32A32_SFLOAT, image_dzCoefficients.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
	
		ByteBuffer pushConstants = memAlloc(Integer.BYTES * 2);
		pushConstants.putInt(N);
		pushConstants.putInt(L);
		pushConstants.flip();
		
		ByteBuffer ubo = memAlloc(Float.BYTES * 1);
		ubo.putFloat(t);
		ubo.flip();
		
		buffer = new VkUniformBuffer(VkContext.getLogicalDevice().getHandle(),
				VkContext.getPhysicalDevice().getMemoryProperties(),ubo);
		
		descriptor = new CoefficientsDescriptor(device, tilde_h0k, tilde_h0minusk);
		
		pipeline = new VkPipeline(device);
		pipeline.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, Integer.BYTES * 2);
		pipeline.setLayout(descriptor.getDescriptorSetLayout().getHandlePointer());
		pipeline.createComputePipeline(new ShaderModule(device, "fft/hkt.comp.spv", VK_SHADER_STAGE_COMPUTE_BIT));
		
		commandBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				pipeline.getHandle(), pipeline.getLayoutHandle(),
				VkUtil.createLongArray(descriptor.getDescriptorSet()), N/16, N/16, 1,
				pushConstants, VK_SHADER_STAGE_COMPUTE_BIT);

		fence = new Fence(device);
		
		submitInfo = new SubmitInfo();
		submitInfo.setFence(fence);
		submitInfo.setCommandBuffers(commandBuffer.getHandlePointer());
	}
	
	public void render(){
		
		t += (System.currentTimeMillis() - systemTime) / t_delta;
		
		float[] v = {t};
		
		buffer.mapMemory(BufferUtil.createByteBuffer(v));
		
		submitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());
		
		systemTime = System.currentTimeMillis();
	}
	
	@Override
	public void shutdown(){
		
		super.shutdown();
	}
	
}
