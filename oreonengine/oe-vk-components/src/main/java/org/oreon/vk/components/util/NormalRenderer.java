package org.oreon.vk.components.util;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32B32A32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_GENERAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_STORAGE_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_COMPUTE_BIT;

import java.nio.ByteBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.SubmitInfo;
import org.oreon.core.vk.context.VkContext;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.image.VkImage;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.core.vk.image.VkSampler;
import org.oreon.core.vk.pipeline.ShaderModule;
import org.oreon.core.vk.pipeline.VkPipeline;
import org.oreon.core.vk.util.VkUtil;
import org.oreon.core.vk.wrapper.command.ComputeCmdBuffer;
import org.oreon.core.vk.wrapper.image.Image2DLocal;

import lombok.Getter;

public class NormalRenderer {

	private CommandBuffer commandBuffer;
	private VkPipeline pipeline;
	private DescriptorSet descriptorSet;
	private DescriptorSetLayout descriptorSetLayout;
	private SubmitInfo submitInfo;
	private VkImage normalImage;
	
	@Getter
	private VkImageView normalImageView;
	
	public NormalRenderer(VkDevice device,
			VkPhysicalDeviceMemoryProperties memoryProperties,
			int N, float strength,
			VkImageView heightImageView, VkSampler heightSampler) {

		normalImage = new Image2DLocal(device, memoryProperties, N, N,
				VK_FORMAT_R32G32B32A32_SFLOAT,
				VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);
		
		normalImageView = new VkImageView(device,
				VK_FORMAT_R32G32B32A32_SFLOAT, normalImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		descriptorSetLayout = new DescriptorSetLayout(device, 2);
		descriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		descriptorSetLayout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		descriptorSetLayout.create();
		
		descriptorSet = new DescriptorSet(device,
	    		VkContext.getDescriptorPoolManager().getDescriptorPool("POOL_1").getHandle(),
	    		descriptorSetLayout.getHandlePointer());
	    descriptorSet.updateDescriptorImageBuffer(normalImageView.getHandle(),
	    		VK_IMAGE_LAYOUT_GENERAL, -1,
	    		0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
	    descriptorSet.updateDescriptorImageBuffer(heightImageView.getHandle(),
	    		VK_IMAGE_LAYOUT_GENERAL, heightSampler.getHandle(),
	    		1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
		
		ByteBuffer pushConstants = memAlloc(Integer.BYTES + Float.BYTES);
		pushConstants.putInt(N);
		pushConstants.putFloat(strength);
		pushConstants.flip();
		
		pipeline = new VkPipeline(device);
		pipeline.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, Integer.BYTES + Float.BYTES);
		pipeline.setLayout(descriptorSetLayout.getHandlePointer());
		pipeline.createComputePipeline(new ShaderModule(device, "util/normals.comp.spv", VK_SHADER_STAGE_COMPUTE_BIT));
		
		commandBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				pipeline.getHandle(), pipeline.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSet), N/16, N/16, 1,
				pushConstants, VK_SHADER_STAGE_COMPUTE_BIT);
		
		submitInfo = new SubmitInfo();
		submitInfo.setCommandBuffers(commandBuffer.getHandlePointer());
	}
	
	public void render(){
		
		submitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());
	}
}
