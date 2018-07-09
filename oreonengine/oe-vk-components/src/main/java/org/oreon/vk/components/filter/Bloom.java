package org.oreon.vk.components.filter;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE;
import static org.lwjgl.vulkan.VK10.VK_FILTER_LINEAR;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R16G16B16A16_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_GENERAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_STORAGE_BIT;
import static org.lwjgl.vulkan.VK10.VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE;
import static org.lwjgl.vulkan.VK10.VK_SAMPLER_MIPMAP_MODE_LINEAR;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_COMPUTE_BIT;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkQueue;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.SubmitInfo;
import org.oreon.core.vk.descriptor.DescriptorPool;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.device.VkDeviceBundle;
import org.oreon.core.vk.image.VkImage;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.core.vk.image.VkSampler;
import org.oreon.core.vk.pipeline.ShaderModule;
import org.oreon.core.vk.pipeline.VkPipeline;
import org.oreon.core.vk.synchronization.Fence;
import org.oreon.core.vk.util.VkUtil;
import org.oreon.core.vk.wrapper.command.ComputeCmdBuffer;
import org.oreon.core.vk.wrapper.image.Image2DDeviceLocal;
import org.oreon.core.vk.wrapper.image.VkImageBundle;
import org.oreon.core.vk.wrapper.shader.ComputeShader;

public class Bloom {

	private VkQueue queue;
	
	private VkImageBundle bloomSceneImageBundle;
	private VkImageBundle additiveBlendImageBundle;
	private VkImageBundle sceneBrightnessImageBundle;
	private VkImageBundle horizontalBloomBlurImageBundle_div2;
	private VkImageBundle horizontalBloomBlurImageBundle_div4;
	private VkImageBundle horizontalBloomBlurImageBundle_div8;
	private VkImageBundle horizontalBloomBlurImageBundle_div16;
	private VkImageBundle verticalBloomBlurImageBundle_div2;
	private VkImageBundle verticalBloomBlurImageBundle_div4;
	private VkImageBundle verticalBloomBlurImageBundle_div8;
	private VkImageBundle verticalBloomBlurImageBundle_div16;
	
	private Fence fence;
	
	// scene brightness resources
	private VkPipeline sceneBrightnessPipeline;
	private DescriptorSet sceneBrightnessDescriptorSet;
	private DescriptorSetLayout sceneBrightnessDescriptorSetLayout;
	private CommandBuffer sceneBrightnessCmdBuffer;
	private SubmitInfo sceneBrightnessSubmitInfo;
	
	// horizontal DIV 2 blur resources
	private VkPipeline horizontalBlurPipeline_div2;
	private DescriptorSet horizontalBlurDescriptorSet_div2;
	private DescriptorSetLayout horizontalBlurDescriptorSetLayout_div2;
	private CommandBuffer horizontalBlurCmdBuffer_div2;
	private SubmitInfo horizontalBlurSubmitInfo_div2;
	
	// horizontal DIV 4 blur resources
	private VkPipeline horizontalBlurPipeline_div4;
	private DescriptorSet horizontalBlurDescriptorSet_div4;
	private DescriptorSetLayout horizontalBlurDescriptorSetLayout_div4;
	private CommandBuffer horizontalBlurCmdBuffer_div4;
	private SubmitInfo horizontalBlurSubmitInfo_div4;
	
	// horizontal DIV 8 blur resources
	private VkPipeline horizontalBlurPipeline_div8;
	private DescriptorSet horizontalBlurDescriptorSet_div8;
	private DescriptorSetLayout horizontalBlurDescriptorSetLayout_div8;
	private CommandBuffer horizontalBlurCmdBuffer_div8;
	private SubmitInfo horizontalBlurSubmitInfo_div8;
	
	// horizontal DIV 16 blur resources
	private VkPipeline horizontalBlurPipeline_div16;
	private DescriptorSet horizontalBlurDescriptorSet_div16;
	private DescriptorSetLayout horizontalBlurDescriptorSetLayout_div16;
	private CommandBuffer horizontalBlurCmdBuffer_div16;
	private SubmitInfo horizontalBlurSubmitInfo_div16;
	
	// vertical DIV 2 blur resources
	private VkPipeline verticalBlurPipeline_div2;
	private DescriptorSet verticalBlurDescriptorSet_div2;
	private DescriptorSetLayout verticalBlurDescriptorSetLayout_div2;
	private CommandBuffer verticalBlurCmdBuffer_div2;
	private SubmitInfo verticalBlurSubmitInfo_div2;
	
	// vertical DIV 4 blur resources
	private VkPipeline verticalBlurPipeline_div4;
	private DescriptorSet verticalBlurDescriptorSet_div4;
	private DescriptorSetLayout verticalBlurDescriptorSetLayout_div4;
	private CommandBuffer verticalBlurCmdBuffer_div4;
	private SubmitInfo verticalBlurSubmitInfo_div4;
	
	// vertical DIV 8 blur resources
	private VkPipeline verticalBlurPipeline_div8;
	private DescriptorSet verticalBlurDescriptorSet_div8;
	private DescriptorSetLayout verticalBlurDescriptorSetLayout_div8;
	private CommandBuffer verticalBlurCmdBuffer_div8;
	private SubmitInfo verticalBlurSubmitInfo_div8;
	
	// vertical DIV 16 blur resources
	private VkPipeline verticalBlurPipeline_div16;
	private DescriptorSet verticalBlurDescriptorSet_div16;
	private DescriptorSetLayout verticalBlurDescriptorSetLayout_div16;
	private CommandBuffer verticalBlurCmdBuffer_div16;
	private SubmitInfo verticalBlurSubmitInfo_div16;
	
	// blend resources
	private VkPipeline blendPipeline;
	private DescriptorSet blendDescriptorSet;
	private DescriptorSetLayout blendDescriptorSetLayout;
	private CommandBuffer blendCmdBuffer;
	private SubmitInfo blendSubmitInfo;
	private VkSampler bloomBlurSampler_div2;
	private VkSampler bloomBlurSampler_div4;
	private VkSampler bloomBlurSampler_div8;
	private VkSampler bloomBlurSampler_div16;
	
	// final bloom Scene resources
	private VkPipeline bloomScenePipeline;
	private DescriptorSet bloomSceneDescriptorSet;
	private DescriptorSetLayout bloomSceneDescriptorSetLayout;
	private CommandBuffer bloomSceneCmdBuffer;
	private SubmitInfo bloomSceneSubmitInfo;
	
	public Bloom(VkDeviceBundle deviceBundle,
			int width ,int height, VkImageView sceneImageView) {
		
		VkDevice device = deviceBundle.getLogicalDevice().getHandle();
		VkPhysicalDeviceMemoryProperties memoryProperties = deviceBundle.getPhysicalDevice().getMemoryProperties();
		DescriptorPool descriptorPool = deviceBundle.getLogicalDevice().getDescriptorPool(Thread.currentThread().getId());
		queue = deviceBundle.getLogicalDevice().getComputeQueue();
		
		initializeImages(device, memoryProperties, width, height);
		
		fence = new Fence(device);
		
		List<DescriptorSet> descriptorSets = new ArrayList<DescriptorSet>();
		List<DescriptorSetLayout> descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		ShaderModule horizontalBlurShader = new ComputeShader(device,
				"shaders/filter/bloom/horizontalGaussianBlur.comp.spv");
		ShaderModule verticalBlurShader = new ComputeShader(device,
				"shaders/filter/bloom/verticalGaussianBlur.comp.spv");
		ShaderModule sceneBrightnessShader = new ComputeShader(device,
				"shaders/filter/bloom/sceneBrightness.comp.spv");
		ShaderModule additiveBlendShader = new ComputeShader(device,
				"shaders/filter/bloom/additiveBlend.comp.spv");
		ShaderModule bloomSceneShader = new ComputeShader(device,
				"shaders/filter/bloom/bloomScene.comp.spv");
		
		int pushConstantRange = Float.BYTES * 2;
		ByteBuffer pushConstants = memAlloc(pushConstantRange);
		pushConstants.putFloat(width);
		pushConstants.putFloat(height);
		pushConstants.flip();
		
		ByteBuffer pushConstants_div2 = memAlloc(pushConstantRange);
		pushConstants_div2.putFloat(width/2.0f);
		pushConstants_div2.putFloat(height/2.0f);
		pushConstants_div2.flip();
		
		ByteBuffer pushConstants_div4 = memAlloc(pushConstantRange);
		pushConstants_div4.putFloat(width/4.0f);
		pushConstants_div4.putFloat(height/4.0f);
		pushConstants_div4.flip();
		
		ByteBuffer pushConstants_div8 = memAlloc(pushConstantRange);
		pushConstants_div8.putFloat(width/8.0f);
		pushConstants_div8.putFloat(height/8.0f);
		pushConstants_div8.flip();
		
		ByteBuffer pushConstants_div16 = memAlloc(pushConstantRange);
		pushConstants_div16.putFloat(width/16.0f);
		pushConstants_div16.putFloat(height/16.0f);
		pushConstants_div16.flip();
		
		// scene brightness
		
		sceneBrightnessDescriptorSetLayout = new DescriptorSetLayout(device, 2);
		sceneBrightnessDescriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		sceneBrightnessDescriptorSetLayout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		sceneBrightnessDescriptorSetLayout.create();
		
		sceneBrightnessDescriptorSet = new DescriptorSet(device, descriptorPool.getHandle(),
	    		sceneBrightnessDescriptorSetLayout.getHandlePointer());
		sceneBrightnessDescriptorSet.updateDescriptorImageBuffer(
				sceneImageView.getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, -1, 0,
		    	VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		sceneBrightnessDescriptorSet.updateDescriptorImageBuffer(
				sceneBrightnessImageBundle.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, -1, 1,
				VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		
		descriptorSets.add(sceneBrightnessDescriptorSet);
		descriptorSetLayouts.add(sceneBrightnessDescriptorSetLayout);
		
		sceneBrightnessPipeline = new VkPipeline(device);
		sceneBrightnessPipeline.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		sceneBrightnessPipeline.createComputePipeline(sceneBrightnessShader);
		
		sceneBrightnessCmdBuffer = new ComputeCmdBuffer(device,
				deviceBundle.getLogicalDevice().getComputeCommandPool().getHandle(),
				sceneBrightnessPipeline.getHandle(), sceneBrightnessPipeline.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets), width/8, height/8, 1);
		
		sceneBrightnessSubmitInfo = new SubmitInfo();
		sceneBrightnessSubmitInfo.setCommandBuffers(sceneBrightnessCmdBuffer.getHandlePointer());
		sceneBrightnessSubmitInfo.setFence(fence);
		
		descriptorSets = new ArrayList<DescriptorSet>();
		descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		// horizontal blur
		
		// DIV 2
		
		horizontalBlurDescriptorSetLayout_div2 = new DescriptorSetLayout(device, 2);
		horizontalBlurDescriptorSetLayout_div2.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		horizontalBlurDescriptorSetLayout_div2.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		horizontalBlurDescriptorSetLayout_div2.create();
		
		horizontalBlurDescriptorSet_div2 = new DescriptorSet(device, descriptorPool.getHandle(),
	    		horizontalBlurDescriptorSetLayout_div2.getHandlePointer());
		horizontalBlurDescriptorSet_div2.updateDescriptorImageBuffer(
				horizontalBloomBlurImageBundle_div2.getImageView().getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, -1, 0,
		    	VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		horizontalBlurDescriptorSet_div2.updateDescriptorImageBuffer(
				sceneBrightnessImageBundle.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, sceneBrightnessImageBundle.getSampler().getHandle(), 1,
				VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
		
		descriptorSets.add(horizontalBlurDescriptorSet_div2);
		descriptorSetLayouts.add(horizontalBlurDescriptorSetLayout_div2);
		
		horizontalBlurPipeline_div2 = new VkPipeline(device);
		horizontalBlurPipeline_div2.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		horizontalBlurPipeline_div2.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		horizontalBlurPipeline_div2.createComputePipeline(horizontalBlurShader);
		
		horizontalBlurCmdBuffer_div2 = new ComputeCmdBuffer(device,
				deviceBundle.getLogicalDevice().getComputeCommandPool().getHandle(),
				horizontalBlurPipeline_div2.getHandle(), horizontalBlurPipeline_div2.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets), width/16, height/16, 1,
				pushConstants_div2, VK_SHADER_STAGE_COMPUTE_BIT);
		
		horizontalBlurSubmitInfo_div2 = new SubmitInfo();
		horizontalBlurSubmitInfo_div2.setCommandBuffers(horizontalBlurCmdBuffer_div2.getHandlePointer());
		horizontalBlurSubmitInfo_div2.setFence(fence);
		
		descriptorSets = new ArrayList<DescriptorSet>();
		descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		// DIV 4
		
		horizontalBlurDescriptorSetLayout_div4 = new DescriptorSetLayout(device, 2);
		horizontalBlurDescriptorSetLayout_div4.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		horizontalBlurDescriptorSetLayout_div4.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		horizontalBlurDescriptorSetLayout_div4.create();
		
		horizontalBlurDescriptorSet_div4 = new DescriptorSet(device, descriptorPool.getHandle(),
	    		horizontalBlurDescriptorSetLayout_div4.getHandlePointer());
		horizontalBlurDescriptorSet_div4.updateDescriptorImageBuffer(
				horizontalBloomBlurImageBundle_div4.getImageView().getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, -1, 0,
		    	VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		horizontalBlurDescriptorSet_div4.updateDescriptorImageBuffer(
				sceneBrightnessImageBundle.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, sceneBrightnessImageBundle.getSampler().getHandle(), 1,
				VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
		
		descriptorSets.add(horizontalBlurDescriptorSet_div4);
		descriptorSetLayouts.add(horizontalBlurDescriptorSetLayout_div4);
		
		horizontalBlurPipeline_div4 = new VkPipeline(device);
		horizontalBlurPipeline_div4.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		horizontalBlurPipeline_div4.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		horizontalBlurPipeline_div4.createComputePipeline(horizontalBlurShader);
		
		horizontalBlurCmdBuffer_div4 = new ComputeCmdBuffer(device,
				deviceBundle.getLogicalDevice().getComputeCommandPool().getHandle(),
				horizontalBlurPipeline_div4.getHandle(), horizontalBlurPipeline_div4.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets), width/32, height/32, 1,
				pushConstants_div4, VK_SHADER_STAGE_COMPUTE_BIT);
		
		horizontalBlurSubmitInfo_div4 = new SubmitInfo();
		horizontalBlurSubmitInfo_div4.setCommandBuffers(horizontalBlurCmdBuffer_div4.getHandlePointer());
		horizontalBlurSubmitInfo_div4.setFence(fence);
		
		descriptorSets = new ArrayList<DescriptorSet>();
		descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		// DIV 8
		
		horizontalBlurDescriptorSetLayout_div8 = new DescriptorSetLayout(device, 2);
		horizontalBlurDescriptorSetLayout_div8.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		horizontalBlurDescriptorSetLayout_div8.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		horizontalBlurDescriptorSetLayout_div8.create();
		
		horizontalBlurDescriptorSet_div8 = new DescriptorSet(device, descriptorPool.getHandle(),
	    		horizontalBlurDescriptorSetLayout_div8.getHandlePointer());
		horizontalBlurDescriptorSet_div8.updateDescriptorImageBuffer(
				horizontalBloomBlurImageBundle_div8.getImageView().getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, -1, 0,
		    	VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		horizontalBlurDescriptorSet_div8.updateDescriptorImageBuffer(
				sceneBrightnessImageBundle.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, sceneBrightnessImageBundle.getSampler().getHandle(), 1,
				VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
		
		descriptorSets.add(horizontalBlurDescriptorSet_div8);
		descriptorSetLayouts.add(horizontalBlurDescriptorSetLayout_div8);
		
		horizontalBlurPipeline_div8 = new VkPipeline(device);
		horizontalBlurPipeline_div8.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		horizontalBlurPipeline_div8.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		horizontalBlurPipeline_div8.createComputePipeline(horizontalBlurShader);
		
		horizontalBlurCmdBuffer_div8 = new ComputeCmdBuffer(device,
				deviceBundle.getLogicalDevice().getComputeCommandPool().getHandle(),
				horizontalBlurPipeline_div8.getHandle(), horizontalBlurPipeline_div8.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets), width/64, height/64, 1,
				pushConstants_div8, VK_SHADER_STAGE_COMPUTE_BIT);
		
		horizontalBlurSubmitInfo_div8 = new SubmitInfo();
		horizontalBlurSubmitInfo_div8.setCommandBuffers(horizontalBlurCmdBuffer_div8.getHandlePointer());
		horizontalBlurSubmitInfo_div8.setFence(fence);
		
		descriptorSets = new ArrayList<DescriptorSet>();
		descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		// DIV 16
		
		horizontalBlurDescriptorSetLayout_div16 = new DescriptorSetLayout(device, 2);
		horizontalBlurDescriptorSetLayout_div16.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		horizontalBlurDescriptorSetLayout_div16.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		horizontalBlurDescriptorSetLayout_div16.create();
		
		horizontalBlurDescriptorSet_div16 = new DescriptorSet(device, descriptorPool.getHandle(),
	    		horizontalBlurDescriptorSetLayout_div16.getHandlePointer());
		horizontalBlurDescriptorSet_div16.updateDescriptorImageBuffer(
				horizontalBloomBlurImageBundle_div16.getImageView().getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, -1, 0,
		    	VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		horizontalBlurDescriptorSet_div16.updateDescriptorImageBuffer(
				sceneBrightnessImageBundle.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, sceneBrightnessImageBundle.getSampler().getHandle(), 1,
				VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
		
		descriptorSets.add(horizontalBlurDescriptorSet_div16);
		descriptorSetLayouts.add(horizontalBlurDescriptorSetLayout_div16);
		
		horizontalBlurPipeline_div16 = new VkPipeline(device);
		horizontalBlurPipeline_div16.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		horizontalBlurPipeline_div16.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		horizontalBlurPipeline_div16.createComputePipeline(horizontalBlurShader);
		
		horizontalBlurCmdBuffer_div16 = new ComputeCmdBuffer(device,
				deviceBundle.getLogicalDevice().getComputeCommandPool().getHandle(),
				horizontalBlurPipeline_div16.getHandle(), horizontalBlurPipeline_div16.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets), width/128, height/128, 1,
				pushConstants_div16, VK_SHADER_STAGE_COMPUTE_BIT);
		
		horizontalBlurSubmitInfo_div16 = new SubmitInfo();
		horizontalBlurSubmitInfo_div16.setCommandBuffers(horizontalBlurCmdBuffer_div16.getHandlePointer());
		horizontalBlurSubmitInfo_div16.setFence(fence);
		
		descriptorSets = new ArrayList<DescriptorSet>();
		descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		// vertical blur
		
		// DIV 2
		
		verticalBlurDescriptorSetLayout_div2 = new DescriptorSetLayout(device, 2);
		verticalBlurDescriptorSetLayout_div2.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		verticalBlurDescriptorSetLayout_div2.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		verticalBlurDescriptorSetLayout_div2.create();
		
		verticalBlurDescriptorSet_div2 = new DescriptorSet(device, descriptorPool.getHandle(),
	    		verticalBlurDescriptorSetLayout_div2.getHandlePointer());
		verticalBlurDescriptorSet_div2.updateDescriptorImageBuffer(
				horizontalBloomBlurImageBundle_div2.getImageView().getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, -1, 0,
		    	VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		verticalBlurDescriptorSet_div2.updateDescriptorImageBuffer(
				verticalBloomBlurImageBundle_div2.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, -1, 1,
				VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		
		descriptorSets.add(verticalBlurDescriptorSet_div2);
		descriptorSetLayouts.add(verticalBlurDescriptorSetLayout_div2);
		
		verticalBlurPipeline_div2 = new VkPipeline(device);
		verticalBlurPipeline_div2.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		verticalBlurPipeline_div2.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		verticalBlurPipeline_div2.createComputePipeline(verticalBlurShader);
		
		verticalBlurCmdBuffer_div2 = new ComputeCmdBuffer(device,
				deviceBundle.getLogicalDevice().getComputeCommandPool().getHandle(),
				verticalBlurPipeline_div2.getHandle(), verticalBlurPipeline_div2.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets), width/16, height/16, 1,
				pushConstants_div2, VK_SHADER_STAGE_COMPUTE_BIT);
		
		verticalBlurSubmitInfo_div2 = new SubmitInfo();
		verticalBlurSubmitInfo_div2.setCommandBuffers(verticalBlurCmdBuffer_div2.getHandlePointer());
		verticalBlurSubmitInfo_div2.setFence(fence);
		
		descriptorSets = new ArrayList<DescriptorSet>();
		descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		// DIV 4
		
		verticalBlurDescriptorSetLayout_div4 = new DescriptorSetLayout(device, 2);
		verticalBlurDescriptorSetLayout_div4.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		verticalBlurDescriptorSetLayout_div4.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		verticalBlurDescriptorSetLayout_div4.create();
		
		verticalBlurDescriptorSet_div4 = new DescriptorSet(device, descriptorPool.getHandle(),
	    		verticalBlurDescriptorSetLayout_div4.getHandlePointer());
		verticalBlurDescriptorSet_div4.updateDescriptorImageBuffer(
				horizontalBloomBlurImageBundle_div4.getImageView().getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, -1, 0,
		    	VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		verticalBlurDescriptorSet_div4.updateDescriptorImageBuffer(
				verticalBloomBlurImageBundle_div4.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, -1, 1,
				VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		
		descriptorSets.add(verticalBlurDescriptorSet_div4);
		descriptorSetLayouts.add(verticalBlurDescriptorSetLayout_div4);
		
		verticalBlurPipeline_div4 = new VkPipeline(device);
		verticalBlurPipeline_div4.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		verticalBlurPipeline_div4.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		verticalBlurPipeline_div4.createComputePipeline(verticalBlurShader);
		
		verticalBlurCmdBuffer_div4 = new ComputeCmdBuffer(device,
				deviceBundle.getLogicalDevice().getComputeCommandPool().getHandle(),
				verticalBlurPipeline_div4.getHandle(), verticalBlurPipeline_div4.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets), width/32, height/32, 1,
				pushConstants_div4, VK_SHADER_STAGE_COMPUTE_BIT);
		
		verticalBlurSubmitInfo_div4 = new SubmitInfo();
		verticalBlurSubmitInfo_div4.setCommandBuffers(verticalBlurCmdBuffer_div4.getHandlePointer());
		verticalBlurSubmitInfo_div4.setFence(fence);
		
		descriptorSets = new ArrayList<DescriptorSet>();
		descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		// DIV 8
		
		verticalBlurDescriptorSetLayout_div8 = new DescriptorSetLayout(device, 2);
		verticalBlurDescriptorSetLayout_div8.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		verticalBlurDescriptorSetLayout_div8.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		verticalBlurDescriptorSetLayout_div8.create();
		
		verticalBlurDescriptorSet_div8 = new DescriptorSet(device, descriptorPool.getHandle(),
	    		verticalBlurDescriptorSetLayout_div8.getHandlePointer());
		verticalBlurDescriptorSet_div8.updateDescriptorImageBuffer(
				horizontalBloomBlurImageBundle_div8.getImageView().getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, -1, 0,
		    	VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		verticalBlurDescriptorSet_div8.updateDescriptorImageBuffer(
				verticalBloomBlurImageBundle_div8.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, -1, 1,
				VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		
		descriptorSets.add(verticalBlurDescriptorSet_div8);
		descriptorSetLayouts.add(verticalBlurDescriptorSetLayout_div8);
		
		verticalBlurPipeline_div8 = new VkPipeline(device);
		verticalBlurPipeline_div8.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		verticalBlurPipeline_div8.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		verticalBlurPipeline_div8.createComputePipeline(verticalBlurShader);
		
		verticalBlurCmdBuffer_div8 = new ComputeCmdBuffer(device,
				deviceBundle.getLogicalDevice().getComputeCommandPool().getHandle(),
				verticalBlurPipeline_div8.getHandle(), verticalBlurPipeline_div8.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets), width/64, height/64, 1,
				pushConstants_div8, VK_SHADER_STAGE_COMPUTE_BIT);
		
		verticalBlurSubmitInfo_div8 = new SubmitInfo();
		verticalBlurSubmitInfo_div8.setCommandBuffers(verticalBlurCmdBuffer_div8.getHandlePointer());
		verticalBlurSubmitInfo_div8.setFence(fence);
		
		descriptorSets = new ArrayList<DescriptorSet>();
		descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		// DIV 16
		
		verticalBlurDescriptorSetLayout_div16 = new DescriptorSetLayout(device, 2);
		verticalBlurDescriptorSetLayout_div16.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		verticalBlurDescriptorSetLayout_div16.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		verticalBlurDescriptorSetLayout_div16.create();
		
		verticalBlurDescriptorSet_div16 = new DescriptorSet(device, descriptorPool.getHandle(),
	    		verticalBlurDescriptorSetLayout_div16.getHandlePointer());
		verticalBlurDescriptorSet_div16.updateDescriptorImageBuffer(
				horizontalBloomBlurImageBundle_div16.getImageView().getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, -1, 0,
		    	VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		verticalBlurDescriptorSet_div16.updateDescriptorImageBuffer(
				verticalBloomBlurImageBundle_div16.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, -1, 1,
				VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		
		descriptorSets.add(verticalBlurDescriptorSet_div16);
		descriptorSetLayouts.add(verticalBlurDescriptorSetLayout_div16);
		
		verticalBlurPipeline_div16 = new VkPipeline(device);
		verticalBlurPipeline_div16.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		verticalBlurPipeline_div16.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		verticalBlurPipeline_div16.createComputePipeline(verticalBlurShader);
		
		verticalBlurCmdBuffer_div16 = new ComputeCmdBuffer(device,
				deviceBundle.getLogicalDevice().getComputeCommandPool().getHandle(),
				verticalBlurPipeline_div16.getHandle(), verticalBlurPipeline_div16.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets), width/128, height/128, 1,
				pushConstants_div16, VK_SHADER_STAGE_COMPUTE_BIT);
		
		verticalBlurSubmitInfo_div16 = new SubmitInfo();
		verticalBlurSubmitInfo_div16.setCommandBuffers(verticalBlurCmdBuffer_div16.getHandlePointer());
		verticalBlurSubmitInfo_div16.setFence(fence);
		
		descriptorSets = new ArrayList<DescriptorSet>();
		descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		// aditive Blend
		
		bloomBlurSampler_div2 = new VkSampler(device, VK_FILTER_LINEAR,
				false, 0, VK_SAMPLER_MIPMAP_MODE_LINEAR, 0, VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
		bloomBlurSampler_div4 = new VkSampler(device, VK_FILTER_LINEAR,
				false, 0, VK_SAMPLER_MIPMAP_MODE_LINEAR, 0, VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
		bloomBlurSampler_div8 = new VkSampler(device, VK_FILTER_LINEAR,
				false, 0, VK_SAMPLER_MIPMAP_MODE_LINEAR, 0, VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
		bloomBlurSampler_div16 = new VkSampler(device, VK_FILTER_LINEAR,
				false, 0, VK_SAMPLER_MIPMAP_MODE_LINEAR, 0, VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
		
		blendDescriptorSetLayout = new DescriptorSetLayout(device, 5);
		blendDescriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		blendDescriptorSetLayout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		blendDescriptorSetLayout.addLayoutBinding(2, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		blendDescriptorSetLayout.addLayoutBinding(3, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		blendDescriptorSetLayout.addLayoutBinding(4, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		blendDescriptorSetLayout.create();
		
		blendDescriptorSet = new DescriptorSet(device, descriptorPool.getHandle(),
	    		blendDescriptorSetLayout.getHandlePointer());
		blendDescriptorSet.updateDescriptorImageBuffer(
				additiveBlendImageBundle.getImageView().getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, -1, 0,
		    	VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		blendDescriptorSet.updateDescriptorImageBuffer(
				verticalBloomBlurImageBundle_div2.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, bloomBlurSampler_div2.getHandle(), 1,
				VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
		blendDescriptorSet.updateDescriptorImageBuffer(
				verticalBloomBlurImageBundle_div4.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, bloomBlurSampler_div4.getHandle(), 2,
				VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
		blendDescriptorSet.updateDescriptorImageBuffer(
				verticalBloomBlurImageBundle_div8.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, bloomBlurSampler_div8.getHandle(), 3,
				VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
		blendDescriptorSet.updateDescriptorImageBuffer(
				verticalBloomBlurImageBundle_div16.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, bloomBlurSampler_div16.getHandle(), 4,
				VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
		
		descriptorSets.add(blendDescriptorSet);
		descriptorSetLayouts.add(blendDescriptorSetLayout);
		
		blendPipeline = new VkPipeline(device);
		blendPipeline.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		blendPipeline.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		blendPipeline.createComputePipeline(additiveBlendShader);
		
		blendCmdBuffer = new ComputeCmdBuffer(device,
				deviceBundle.getLogicalDevice().getComputeCommandPool().getHandle(),
				blendPipeline.getHandle(), blendPipeline.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets), width/8, height/8, 1,
				pushConstants, VK_SHADER_STAGE_COMPUTE_BIT);
		
		blendSubmitInfo = new SubmitInfo();
		blendSubmitInfo.setCommandBuffers(blendCmdBuffer.getHandlePointer());
		blendSubmitInfo.setFence(fence);
		
		descriptorSets = new ArrayList<DescriptorSet>();
		descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		// final bloom scene
		
		bloomSceneDescriptorSetLayout = new DescriptorSetLayout(device, 3);
		bloomSceneDescriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		bloomSceneDescriptorSetLayout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		bloomSceneDescriptorSetLayout.addLayoutBinding(2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		bloomSceneDescriptorSetLayout.create();
		
		bloomSceneDescriptorSet = new DescriptorSet(device, descriptorPool.getHandle(),
	    		bloomSceneDescriptorSetLayout.getHandlePointer());
		bloomSceneDescriptorSet.updateDescriptorImageBuffer(
				sceneImageView.getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, -1, 0,
		    	VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		bloomSceneDescriptorSet.updateDescriptorImageBuffer(
				additiveBlendImageBundle.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, -1, 1,
				VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		bloomSceneDescriptorSet.updateDescriptorImageBuffer(
				bloomSceneImageBundle.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, -1, 2,
				VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		
		descriptorSets.add(bloomSceneDescriptorSet);
		descriptorSetLayouts.add(bloomSceneDescriptorSetLayout);
		
		bloomScenePipeline = new VkPipeline(device);
		bloomScenePipeline.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		bloomScenePipeline.createComputePipeline(bloomSceneShader);
		
		bloomSceneCmdBuffer = new ComputeCmdBuffer(device,
				deviceBundle.getLogicalDevice().getComputeCommandPool().getHandle(),
				bloomScenePipeline.getHandle(), bloomScenePipeline.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets), width/8, height/8, 1);
		
		bloomSceneSubmitInfo = new SubmitInfo();
		bloomSceneSubmitInfo.setCommandBuffers(bloomSceneCmdBuffer.getHandlePointer());
		bloomSceneSubmitInfo.setFence(fence);
		
		horizontalBlurShader.destroy();
		verticalBlurShader.destroy();
		additiveBlendShader.destroy();
		sceneBrightnessShader.destroy();
		bloomSceneShader.destroy();
	}
	
	public void render(){
		
		sceneBrightnessSubmitInfo.submit(queue);
		fence.waitForFence();
		horizontalBlurSubmitInfo_div2.submit(queue);
		fence.waitForFence();
		horizontalBlurSubmitInfo_div4.submit(queue);
		fence.waitForFence();
		horizontalBlurSubmitInfo_div8.submit(queue);
		fence.waitForFence();
		horizontalBlurSubmitInfo_div16.submit(queue);
		fence.waitForFence();
		verticalBlurSubmitInfo_div2.submit(queue);
		fence.waitForFence();
		verticalBlurSubmitInfo_div4.submit(queue);
		fence.waitForFence();
		verticalBlurSubmitInfo_div8.submit(queue);
		fence.waitForFence();
		verticalBlurSubmitInfo_div16.submit(queue);
		fence.waitForFence();
		blendSubmitInfo.submit(queue);
		fence.waitForFence();
		bloomSceneSubmitInfo.submit(queue);
		fence.waitForFence();
	}
	
	public void initializeImages(VkDevice device,
			VkPhysicalDeviceMemoryProperties memoryProperties, int width, int height){
		
		VkImage bloomSceneImage = new Image2DDeviceLocal(device, memoryProperties, 
				width, height, VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_USAGE_STORAGE_BIT
				| VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView bloomSceneImageView = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, bloomSceneImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		bloomSceneImageBundle = new VkImageBundle(bloomSceneImage, bloomSceneImageView);
		
		VkImage sceneBrightnessImage = new Image2DDeviceLocal(device, memoryProperties, 
				width, height, VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_USAGE_STORAGE_BIT
				| VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView sceneBrightnessImageView = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, bloomSceneImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		VkSampler brightnessSampler = new VkSampler(device, VK_FILTER_LINEAR,
				false, 0, VK_SAMPLER_MIPMAP_MODE_LINEAR, 0, VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
		
		sceneBrightnessImageBundle = new VkImageBundle(sceneBrightnessImage, sceneBrightnessImageView,
				brightnessSampler);
		
		VkImage additiveBlendImage = new Image2DDeviceLocal(device, memoryProperties, 
				width, height, VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_USAGE_STORAGE_BIT
				| VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView additiveBlendImageView = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, additiveBlendImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		additiveBlendImageBundle = new VkImageBundle(additiveBlendImage, additiveBlendImageView);
		
		// horizontal Bloom Blur images
		
		VkImage horizontalBloomBlurImage_div2 = new Image2DDeviceLocal(device, memoryProperties, 
				(int) (width/2.0f), (int) (height/2.0f), VK_FORMAT_R16G16B16A16_SFLOAT,
				VK_IMAGE_USAGE_STORAGE_BIT | VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView horizontalBloomBlurImageView_div2 = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, horizontalBloomBlurImage_div2.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		horizontalBloomBlurImageBundle_div2 = new VkImageBundle(horizontalBloomBlurImage_div2,
				horizontalBloomBlurImageView_div2);
		
		VkImage horizontalBloomBlurImage_div4 = new Image2DDeviceLocal(device, memoryProperties, 
				(int) (width/4.0f), (int) (height/4.0f), VK_FORMAT_R16G16B16A16_SFLOAT,
				VK_IMAGE_USAGE_STORAGE_BIT | VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView horizontalBloomBlurImageView_div4 = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, horizontalBloomBlurImage_div4.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		horizontalBloomBlurImageBundle_div4 = new VkImageBundle(horizontalBloomBlurImage_div4,
				horizontalBloomBlurImageView_div4);
		
		VkImage horizontalBloomBlurImage_div8 = new Image2DDeviceLocal(device, memoryProperties, 
				(int) (width/8.0f), (int) (height/8.0f), VK_FORMAT_R16G16B16A16_SFLOAT,
				VK_IMAGE_USAGE_STORAGE_BIT | VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView horizontalBloomBlurImageView_div8 = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, horizontalBloomBlurImage_div8.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		horizontalBloomBlurImageBundle_div8 = new VkImageBundle(horizontalBloomBlurImage_div8,
				horizontalBloomBlurImageView_div8);
		
		VkImage horizontalBloomBlurImage_div16 = new Image2DDeviceLocal(device, memoryProperties, 
				(int) (width/16.0f), (int) (height/16.0f), VK_FORMAT_R16G16B16A16_SFLOAT,
				VK_IMAGE_USAGE_STORAGE_BIT | VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView horizontalBloomBlurImageView_div16 = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, horizontalBloomBlurImage_div16.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		horizontalBloomBlurImageBundle_div16 = new VkImageBundle(horizontalBloomBlurImage_div16,
				horizontalBloomBlurImageView_div16);
		
		// vertical Bloom Blur images
		
		VkImage verticalBloomBlurImage_div2 = new Image2DDeviceLocal(device, memoryProperties, 
				(int) (width/2.0f), (int) (height/2.0f), VK_FORMAT_R16G16B16A16_SFLOAT,
				VK_IMAGE_USAGE_STORAGE_BIT | VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView verticalBloomBlurImageView_div2 = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, verticalBloomBlurImage_div2.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		verticalBloomBlurImageBundle_div2 = new VkImageBundle(verticalBloomBlurImage_div2,
				verticalBloomBlurImageView_div2);
		
		VkImage verticalBloomBlurImage_div4 = new Image2DDeviceLocal(device, memoryProperties, 
				(int) (width/4.0f), (int) (height/4.0f), VK_FORMAT_R16G16B16A16_SFLOAT,
				VK_IMAGE_USAGE_STORAGE_BIT | VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView verticalBloomBlurImageView_div4 = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, verticalBloomBlurImage_div4.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		verticalBloomBlurImageBundle_div4 = new VkImageBundle(verticalBloomBlurImage_div4,
				verticalBloomBlurImageView_div4);
		
		VkImage verticalBloomBlurImage_div8 = new Image2DDeviceLocal(device, memoryProperties, 
				(int) (width/8.0f), (int) (height/8.0f), VK_FORMAT_R16G16B16A16_SFLOAT,
				VK_IMAGE_USAGE_STORAGE_BIT | VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView verticalBloomBlurImageView_div8 = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, verticalBloomBlurImage_div8.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		verticalBloomBlurImageBundle_div8 = new VkImageBundle(verticalBloomBlurImage_div8,
				verticalBloomBlurImageView_div8);
		
		VkImage verticalBloomBlurImage_div16 = new Image2DDeviceLocal(device, memoryProperties, 
				(int) (width/16.0f), (int) (height/16.0f), VK_FORMAT_R16G16B16A16_SFLOAT,
				VK_IMAGE_USAGE_STORAGE_BIT | VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView verticalBloomBlurImageView_div16 = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, verticalBloomBlurImage_div16.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		verticalBloomBlurImageBundle_div16 = new VkImageBundle(verticalBloomBlurImage_div16,
				verticalBloomBlurImageView_div16);
	}
	
	public void shutdown(){
		bloomSceneImageBundle.destroy();
		additiveBlendImageBundle.destroy();
		sceneBrightnessImageBundle.destroy();
		horizontalBloomBlurImageBundle_div2.destroy();
		horizontalBloomBlurImageBundle_div4.destroy();
		horizontalBloomBlurImageBundle_div8.destroy();
		horizontalBloomBlurImageBundle_div16.destroy();
		verticalBloomBlurImageBundle_div2.destroy();
		verticalBloomBlurImageBundle_div4.destroy();
		verticalBloomBlurImageBundle_div8.destroy();
		verticalBloomBlurImageBundle_div16.destroy();
		fence.destroy();
		sceneBrightnessPipeline.destroy();
		sceneBrightnessDescriptorSet.destroy();
		sceneBrightnessDescriptorSetLayout.destroy();
		sceneBrightnessCmdBuffer.destroy();
		horizontalBlurPipeline_div2.destroy();
		horizontalBlurDescriptorSet_div2.destroy();
		horizontalBlurDescriptorSetLayout_div2.destroy();
		horizontalBlurCmdBuffer_div2.destroy();
		horizontalBlurPipeline_div4.destroy();
		horizontalBlurDescriptorSet_div4.destroy();
		horizontalBlurDescriptorSetLayout_div4.destroy();
		horizontalBlurCmdBuffer_div4.destroy();
		horizontalBlurPipeline_div8.destroy();
		horizontalBlurDescriptorSet_div8.destroy();
		horizontalBlurDescriptorSetLayout_div8.destroy();
		horizontalBlurCmdBuffer_div8.destroy();
		horizontalBlurPipeline_div16.destroy();
		horizontalBlurDescriptorSet_div16.destroy();
		horizontalBlurDescriptorSetLayout_div16.destroy();
		horizontalBlurCmdBuffer_div16.destroy();
		verticalBlurPipeline_div2.destroy();
		verticalBlurDescriptorSet_div2.destroy();
		verticalBlurDescriptorSetLayout_div2.destroy();
		verticalBlurCmdBuffer_div2.destroy();
		verticalBlurPipeline_div4.destroy();
		verticalBlurDescriptorSet_div4.destroy();
		verticalBlurDescriptorSetLayout_div4.destroy();
		verticalBlurCmdBuffer_div4.destroy();
		verticalBlurPipeline_div8.destroy();
		verticalBlurDescriptorSet_div8.destroy();
		verticalBlurDescriptorSetLayout_div8.destroy();
		verticalBlurCmdBuffer_div8.destroy();
		verticalBlurPipeline_div16.destroy();
		verticalBlurDescriptorSet_div16.destroy();
		verticalBlurDescriptorSetLayout_div16.destroy();
		verticalBlurCmdBuffer_div16.destroy();
		blendPipeline.destroy();
		blendDescriptorSet.destroy();
		blendDescriptorSetLayout.destroy();
		blendCmdBuffer.destroy();
		bloomBlurSampler_div2.destroy();
		bloomBlurSampler_div4.destroy();
		bloomBlurSampler_div8.destroy();
		bloomBlurSampler_div16.destroy();
		bloomScenePipeline.destroy();
		bloomSceneDescriptorSet.destroy();
		bloomSceneDescriptorSetLayout.destroy();
		bloomSceneCmdBuffer.destroy();
	}
	
	public VkImageView getBloomSceneImageView(){
		return bloomSceneImageBundle.getImageView();
	}
	
}
