package org.oreon.vk.engine;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE;
import static org.lwjgl.vulkan.VK10.VK_FILTER_LINEAR;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R16G16B16A16_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_GENERAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_STORAGE_BIT;
import static org.lwjgl.vulkan.VK10.VK_SAMPLER_ADDRESS_MODE_REPEAT;
import static org.lwjgl.vulkan.VK10.VK_SAMPLER_MIPMAP_MODE_NEAREST;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_COMPUTE_BIT;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkQueue;
import org.oreon.core.context.EngineContext;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.SubmitInfo;
import org.oreon.core.vk.command.VkCmdRecordUtil;
import org.oreon.core.vk.descriptor.DescriptorPool;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.device.VkDeviceBundle;
import org.oreon.core.vk.image.VkImage;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.core.vk.image.VkSampler;
import org.oreon.core.vk.pipeline.ShaderModule;
import org.oreon.core.vk.pipeline.VkPipeline;
import org.oreon.core.vk.util.VkUtil;
import org.oreon.core.vk.wrapper.command.ComputeCmdBuffer;
import org.oreon.core.vk.wrapper.image.Image2DDeviceLocal;
import org.oreon.core.vk.wrapper.shader.ComputeShader;

import lombok.Getter;

public class FXAA {
	
	private VkQueue queue;
	
	private VkImage fxaaImage;
	@Getter
	private VkImageView fxaaImageView;
	private VkPipeline computePipeline;
	private DescriptorSet descriptorSet;
	private DescriptorSetLayout descriptorSetLayout;
	private CommandBuffer cmdBuffer;
	private SubmitInfo submitInfo;
	private VkSampler sceneSampler;
	
	private ByteBuffer pushConstants;
	private List<DescriptorSet> descriptorSets;
	private int width;
	private int height;

	public FXAA(VkDeviceBundle deviceBundle, int width ,int height, 
			VkImageView sceneImageView) {
		
		VkDevice device = deviceBundle.getLogicalDevice().getHandle();
		VkPhysicalDeviceMemoryProperties memoryProperties = deviceBundle.getPhysicalDevice().getMemoryProperties();
		DescriptorPool descriptorPool = deviceBundle.getLogicalDevice().getDescriptorPool(Thread.currentThread().getId());
		queue = deviceBundle.getLogicalDevice().getComputeQueue();
		this.width = width;
		this.height = height;
		
		fxaaImage = new Image2DDeviceLocal(device, memoryProperties, 
				width, height, VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_USAGE_STORAGE_BIT
				| VK_IMAGE_USAGE_SAMPLED_BIT);
		fxaaImageView = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, fxaaImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		sceneSampler = new VkSampler(device, VK_FILTER_LINEAR, false, 0,
				VK_SAMPLER_MIPMAP_MODE_NEAREST, 0, VK_SAMPLER_ADDRESS_MODE_REPEAT);
		
		int pushConstantRange = Float.BYTES * 2;
		pushConstants = memAlloc(pushConstantRange);
		pushConstants.putFloat(EngineContext.getConfig().getX_ScreenResolution());
		pushConstants.putFloat(EngineContext.getConfig().getY_ScreenResolution());
		pushConstants.flip();
		
		descriptorSetLayout = new DescriptorSetLayout(device, 3);
		descriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		descriptorSetLayout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		descriptorSetLayout.create();

		descriptorSet = new DescriptorSet(device,
				descriptorPool.getHandle(), descriptorSetLayout.getHandlePointer());
		descriptorSet.updateDescriptorImageBuffer(fxaaImageView.getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, -1, 0,
		    	VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		descriptorSet.updateDescriptorImageBuffer(sceneImageView.getHandle(),
				VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, sceneSampler.getHandle(), 1,
	    		VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
		
		descriptorSets = new ArrayList<DescriptorSet>();
		List<DescriptorSetLayout> descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		descriptorSets.add(descriptorSet);
		descriptorSetLayouts.add(descriptorSetLayout);
		
		ShaderModule shader = new ComputeShader(device, "shaders/fxaa.comp.spv");
		
		computePipeline = new VkPipeline(device);
		computePipeline.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		computePipeline.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		computePipeline.createComputePipeline(shader);
		
		cmdBuffer = new ComputeCmdBuffer(device,
				deviceBundle.getLogicalDevice().getComputeCommandPool().getHandle(),
				computePipeline.getHandle(), computePipeline.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets), width/16, height/16, 1,
				pushConstants, VK_SHADER_STAGE_COMPUTE_BIT);
		
		submitInfo = new SubmitInfo();
		submitInfo.setCommandBuffers(cmdBuffer.getHandlePointer());
		
		shader.destroy();
	}
	
	public void record(VkCommandBuffer commandBuffer){
		VkCmdRecordUtil.cmdPushConstants(commandBuffer, computePipeline.getLayoutHandle(),
				VK_SHADER_STAGE_COMPUTE_BIT, pushConstants);
		VkCmdRecordUtil.cmdBindComputePipeline(commandBuffer, computePipeline.getHandle());
		VkCmdRecordUtil.cmdBindComputeDescriptorSets(commandBuffer,
				computePipeline.getLayoutHandle(), VkUtil.createLongArray(descriptorSets));
		VkCmdRecordUtil.cmdDispatch(commandBuffer, width/16, height/16, 1);
	}
	
	public void render(){
		
		submitInfo.submit(queue);
	}
	
	public void shutdown(){
		fxaaImage.destroy();
		fxaaImageView.destroy();
		computePipeline.destroy();
		descriptorSet.destroy();
		descriptorSetLayout.destroy();
		cmdBuffer.destroy();
		sceneSampler.destroy();
	}
}
