package org.oreon.vk.components.filter;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_SHADER_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_SHADER_WRITE_BIT;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE;
import static org.lwjgl.vulkan.VK10.VK_FILTER_LINEAR;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R16G16B16A16_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_GENERAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_STORAGE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT;
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
import org.oreon.core.vk.wrapper.image.Image2DDeviceLocal;
import org.oreon.core.vk.wrapper.image.VkImageBundle;
import org.oreon.core.vk.wrapper.shader.ComputeShader;

import lombok.Getter;

@Getter
public class Bloom {

	private VkQueue queue;
	
	private VkImageBundle bloomSceneImageBundle;
	private VkImageBundle additiveBlendImageBundle;
	private VkImageBundle sceneBrightnessImageBundle;
	private VkImageBundle sceneBrightnessImageBundle_div4;
	private VkImageBundle sceneBrightnessImageBundle_div8;
	private VkImageBundle sceneBrightnessImageBundle_div16;
	private VkImageBundle horizontalBloomBlurImageBundle_div2;
	private VkImageBundle horizontalBloomBlurImageBundle_div4;
	private VkImageBundle horizontalBloomBlurImageBundle_div8;
	private VkImageBundle horizontalBloomBlurImageBundle_div16;
	private VkImageBundle verticalBloomBlurImageBundle_div2;
	private VkImageBundle verticalBloomBlurImageBundle_div4;
	private VkImageBundle verticalBloomBlurImageBundle_div8;
	private VkImageBundle verticalBloomBlurImageBundle_div16;
	
	// scene brightness resources
	private VkPipeline sceneBrightnessPipeline;
	private DescriptorSet sceneBrightnessDescriptorSet;
	private DescriptorSetLayout sceneBrightnessDescriptorSetLayout;
	private List<DescriptorSet> sceneBrightnessDescriptorSets;
	
	// horizontal DIV 2 blur resources
	private VkPipeline horizontalBlurPipeline_div2;
	private DescriptorSet horizontalBlurDescriptorSet_div2;
	private DescriptorSetLayout horizontalBlurDescriptorSetLayout_div2;
	private List<DescriptorSet> horizontalBlurDescriptorSets_div2;
	
	// horizontal DIV 4 blur resources
	private VkPipeline horizontalBlurPipeline_div4;
	private DescriptorSet horizontalBlurDescriptorSet_div4;
	private DescriptorSetLayout horizontalBlurDescriptorSetLayout_div4;
	private List<DescriptorSet> horizontalBlurDescriptorSets_div4;
	
	// horizontal DIV 8 blur resources
	private VkPipeline horizontalBlurPipeline_div8;
	private DescriptorSet horizontalBlurDescriptorSet_div8;
	private DescriptorSetLayout horizontalBlurDescriptorSetLayout_div8;
	private List<DescriptorSet> horizontalBlurDescriptorSets_div8;
	
	// horizontal DIV 16 blur resources
	private VkPipeline horizontalBlurPipeline_div16;
	private DescriptorSet horizontalBlurDescriptorSet_div16;
	private DescriptorSetLayout horizontalBlurDescriptorSetLayout_div16;
	private List<DescriptorSet> horizontalBlurDescriptorSets_div16;
	
	// vertical DIV 2 blur resources
	private VkPipeline verticalBlurPipeline_div2;
	private DescriptorSet verticalBlurDescriptorSet_div2;
	private DescriptorSetLayout verticalBlurDescriptorSetLayout_div2;
	private List<DescriptorSet> verticalBlurDescriptorSets_div2;
	
	// vertical DIV 4 blur resources
	private VkPipeline verticalBlurPipeline_div4;
	private DescriptorSet verticalBlurDescriptorSet_div4;
	private DescriptorSetLayout verticalBlurDescriptorSetLayout_div4;
	private List<DescriptorSet> verticalBlurDescriptorSets_div4;
	
	// vertical DIV 8 blur resources
	private VkPipeline verticalBlurPipeline_div8;
	private DescriptorSet verticalBlurDescriptorSet_div8;
	private DescriptorSetLayout verticalBlurDescriptorSetLayout_div8;
	private List<DescriptorSet> verticalBlurDescriptorSets_div8;
	
	// vertical DIV 16 blur resources
	private VkPipeline verticalBlurPipeline_div16;
	private DescriptorSet verticalBlurDescriptorSet_div16;
	private DescriptorSetLayout verticalBlurDescriptorSetLayout_div16;
	private List<DescriptorSet> verticalBlurDescriptorSets_div16;
	
	// blend resources
	private VkPipeline blendPipeline;
	private DescriptorSet blendDescriptorSet;
	private DescriptorSetLayout blendDescriptorSetLayout;
	private VkSampler bloomBlurSampler_div2;
	private VkSampler bloomBlurSampler_div4;
	private VkSampler bloomBlurSampler_div8;
	private VkSampler bloomBlurSampler_div16;
	private List<DescriptorSet> blendDescriptorSets;
	
	// final bloom Scene resources
	private VkPipeline bloomScenePipeline;
	private DescriptorSet bloomSceneDescriptorSet;
	private DescriptorSetLayout bloomSceneDescriptorSetLayout;
	private List<DescriptorSet> bloomSceneDescriptorSets;
	
	private ByteBuffer pushConstants_brightness;
	private ByteBuffer pushConstants_blend;
	private ByteBuffer pushConstants_div2;
	private ByteBuffer pushConstants_div4;
	private ByteBuffer pushConstants_div8;
	private ByteBuffer pushConstants_div16;
	
	private int width;
	private int height;
	
	public Bloom(VkDeviceBundle deviceBundle,
			int width, int height, VkImageView sceneImageView, VkImageView specular_emission_bloom_attachment) {
		
		VkDevice device = deviceBundle.getLogicalDevice().getHandle();
		VkPhysicalDeviceMemoryProperties memoryProperties = deviceBundle.getPhysicalDevice().getMemoryProperties();
		DescriptorPool descriptorPool = deviceBundle.getLogicalDevice().getDescriptorPool(Thread.currentThread().getId());
		queue = deviceBundle.getLogicalDevice().getComputeQueue();
		this.width = width;
		this.height = height;
		
		initializeImages(device, memoryProperties, width, height);
		
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
		
		pushConstants_brightness = pushConstants_blend = memAlloc(Float.BYTES * 12);
		pushConstants_brightness.putFloat(width);
		pushConstants_brightness.putFloat(height);
		pushConstants_brightness.putFloat(width/4.0f);
		pushConstants_brightness.putFloat(height/4.0f);
		pushConstants_brightness.putFloat(width/8.0f);
		pushConstants_brightness.putFloat(height/8.0f);
		pushConstants_brightness.putFloat(width/16.0f);
		pushConstants_brightness.putFloat(height/16.0f);
		pushConstants_brightness.putFloat(2);
		pushConstants_brightness.putFloat(4);
		pushConstants_brightness.putFloat(8);
		pushConstants_brightness.putFloat(16);
		pushConstants_brightness.flip();
		
		int pushConstantRange = Float.BYTES * 2;
		pushConstants_blend = memAlloc(pushConstantRange);
		pushConstants_blend.putFloat(width);
		pushConstants_blend.putFloat(height);
		pushConstants_blend.flip();
		
		pushConstants_div2 = memAlloc(pushConstantRange);
		pushConstants_div2.putFloat(width/2.0f);
		pushConstants_div2.putFloat(height/2.0f);
		pushConstants_div2.flip();
		
		pushConstants_div4 = memAlloc(pushConstantRange);
		pushConstants_div4.putFloat(width/4.0f);
		pushConstants_div4.putFloat(height/4.0f);
		pushConstants_div4.flip();
		
		pushConstants_div8 = memAlloc(pushConstantRange);
		pushConstants_div8.putFloat(width/8.0f);
		pushConstants_div8.putFloat(height/8.0f);
		pushConstants_div8.flip();
		
		pushConstants_div16 = memAlloc(pushConstantRange);
		pushConstants_div16.putFloat(width/16.0f);
		pushConstants_div16.putFloat(height/16.0f);
		pushConstants_div16.flip();
		
		// scene brightness
		
		sceneBrightnessDescriptorSetLayout = new DescriptorSetLayout(device, 5);
		sceneBrightnessDescriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		sceneBrightnessDescriptorSetLayout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		sceneBrightnessDescriptorSetLayout.addLayoutBinding(2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		sceneBrightnessDescriptorSetLayout.addLayoutBinding(3, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		sceneBrightnessDescriptorSetLayout.addLayoutBinding(4, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
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
		sceneBrightnessDescriptorSet.updateDescriptorImageBuffer(
				sceneBrightnessImageBundle_div4.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, -1, 2,
				VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		sceneBrightnessDescriptorSet.updateDescriptorImageBuffer(
				sceneBrightnessImageBundle_div8.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, -1, 3,
				VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		sceneBrightnessDescriptorSet.updateDescriptorImageBuffer(
				sceneBrightnessImageBundle_div16.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, -1, 4,
				VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		
		sceneBrightnessDescriptorSets = new ArrayList<DescriptorSet>();
		sceneBrightnessDescriptorSets.add(sceneBrightnessDescriptorSet);
		descriptorSetLayouts.add(sceneBrightnessDescriptorSetLayout);
		
		sceneBrightnessPipeline = new VkPipeline(device);
		sceneBrightnessPipeline.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		sceneBrightnessPipeline.createComputePipeline(sceneBrightnessShader);
		
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
		
		horizontalBlurDescriptorSets_div2 = new ArrayList<DescriptorSet>();
		horizontalBlurDescriptorSets_div2.add(horizontalBlurDescriptorSet_div2);
		descriptorSetLayouts.add(horizontalBlurDescriptorSetLayout_div2);
		
		horizontalBlurPipeline_div2 = new VkPipeline(device);
		horizontalBlurPipeline_div2.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		horizontalBlurPipeline_div2.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		horizontalBlurPipeline_div2.createComputePipeline(horizontalBlurShader);
		
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
				sceneBrightnessImageBundle_div4.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, sceneBrightnessImageBundle_div4.getSampler().getHandle(), 1,
				VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
		
		horizontalBlurDescriptorSets_div4 = new ArrayList<DescriptorSet>();
		horizontalBlurDescriptorSets_div4.add(horizontalBlurDescriptorSet_div4);
		descriptorSetLayouts.add(horizontalBlurDescriptorSetLayout_div4);
		
		horizontalBlurPipeline_div4 = new VkPipeline(device);
		horizontalBlurPipeline_div4.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		horizontalBlurPipeline_div4.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		horizontalBlurPipeline_div4.createComputePipeline(horizontalBlurShader);
		
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
				sceneBrightnessImageBundle_div8.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, sceneBrightnessImageBundle_div8.getSampler().getHandle(), 1,
				VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
		
		horizontalBlurDescriptorSets_div8 = new ArrayList<DescriptorSet>();
		horizontalBlurDescriptorSets_div8.add(horizontalBlurDescriptorSet_div8);
		descriptorSetLayouts.add(horizontalBlurDescriptorSetLayout_div8);
		
		horizontalBlurPipeline_div8 = new VkPipeline(device);
		horizontalBlurPipeline_div8.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		horizontalBlurPipeline_div8.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		horizontalBlurPipeline_div8.createComputePipeline(horizontalBlurShader);
		
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
				sceneBrightnessImageBundle_div16.getImageView().getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, sceneBrightnessImageBundle_div16.getSampler().getHandle(), 1,
				VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
		
		horizontalBlurDescriptorSets_div16 = new ArrayList<DescriptorSet>();
		horizontalBlurDescriptorSets_div16.add(horizontalBlurDescriptorSet_div16);
		descriptorSetLayouts.add(horizontalBlurDescriptorSetLayout_div16);
		
		horizontalBlurPipeline_div16 = new VkPipeline(device);
		horizontalBlurPipeline_div16.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		horizontalBlurPipeline_div16.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		horizontalBlurPipeline_div16.createComputePipeline(horizontalBlurShader);
		
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
		
		verticalBlurDescriptorSets_div2 = new ArrayList<DescriptorSet>();
		verticalBlurDescriptorSets_div2.add(verticalBlurDescriptorSet_div2);
		descriptorSetLayouts.add(verticalBlurDescriptorSetLayout_div2);
		
		verticalBlurPipeline_div2 = new VkPipeline(device);
		verticalBlurPipeline_div2.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		verticalBlurPipeline_div2.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		verticalBlurPipeline_div2.createComputePipeline(verticalBlurShader);
		
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
		
		verticalBlurDescriptorSets_div4 = new ArrayList<DescriptorSet>();
		verticalBlurDescriptorSets_div4.add(verticalBlurDescriptorSet_div4);
		descriptorSetLayouts.add(verticalBlurDescriptorSetLayout_div4);
		
		verticalBlurPipeline_div4 = new VkPipeline(device);
		verticalBlurPipeline_div4.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		verticalBlurPipeline_div4.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		verticalBlurPipeline_div4.createComputePipeline(verticalBlurShader);
		
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
		
		verticalBlurDescriptorSets_div8 = new ArrayList<DescriptorSet>();
		verticalBlurDescriptorSets_div8.add(verticalBlurDescriptorSet_div8);
		descriptorSetLayouts.add(verticalBlurDescriptorSetLayout_div8);
		
		verticalBlurPipeline_div8 = new VkPipeline(device);
		verticalBlurPipeline_div8.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		verticalBlurPipeline_div8.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		verticalBlurPipeline_div8.createComputePipeline(verticalBlurShader);
		
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
		
		verticalBlurDescriptorSets_div16 = new ArrayList<DescriptorSet>();
		verticalBlurDescriptorSets_div16.add(verticalBlurDescriptorSet_div16);
		descriptorSetLayouts.add(verticalBlurDescriptorSetLayout_div16);
		
		verticalBlurPipeline_div16 = new VkPipeline(device);
		verticalBlurPipeline_div16.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		verticalBlurPipeline_div16.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		verticalBlurPipeline_div16.createComputePipeline(verticalBlurShader);
		
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
		
		blendDescriptorSets = new ArrayList<DescriptorSet>();
		blendDescriptorSets.add(blendDescriptorSet);
		descriptorSetLayouts.add(blendDescriptorSetLayout);
		
		blendPipeline = new VkPipeline(device);
		blendPipeline.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		blendPipeline.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		blendPipeline.createComputePipeline(additiveBlendShader);
		
		descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		// final bloom scene
		
		bloomSceneDescriptorSetLayout = new DescriptorSetLayout(device, 4);
		bloomSceneDescriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		bloomSceneDescriptorSetLayout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		bloomSceneDescriptorSetLayout.addLayoutBinding(2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		bloomSceneDescriptorSetLayout.addLayoutBinding(3, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
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
				specular_emission_bloom_attachment.getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, -1, 2,
				VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		bloomSceneDescriptorSet.updateDescriptorImageBuffer(
				bloomSceneImageBundle.getImageView().getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, -1, 3,
		    	VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		
		bloomSceneDescriptorSets = new ArrayList<DescriptorSet>();
		bloomSceneDescriptorSets.add(bloomSceneDescriptorSet);
		descriptorSetLayouts.add(bloomSceneDescriptorSetLayout);
		
		bloomScenePipeline = new VkPipeline(device);
		bloomScenePipeline.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		bloomScenePipeline.createComputePipeline(bloomSceneShader);
		
		horizontalBlurShader.destroy();
		verticalBlurShader.destroy();
		additiveBlendShader.destroy();
		sceneBrightnessShader.destroy();
		bloomSceneShader.destroy();
	}
	
	public void record(CommandBuffer commandBuffer){
		
		// scene luminance
		commandBuffer.pushConstantsCmd(sceneBrightnessPipeline.getLayoutHandle(),
				VK_SHADER_STAGE_COMPUTE_BIT, pushConstants_brightness);
		commandBuffer.bindComputePipelineCmd(sceneBrightnessPipeline.getHandle());
		commandBuffer.bindComputeDescriptorSetsCmd(sceneBrightnessPipeline.getLayoutHandle(),
				VkUtil.createLongArray(sceneBrightnessDescriptorSets));
		commandBuffer.dispatchCmd(width/8, height/8, 1);
		
		// barrier
		commandBuffer.pipelineMemoryBarrierCmd(
	    		VK_ACCESS_SHADER_WRITE_BIT, VK_ACCESS_SHADER_READ_BIT,
	    		VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT,
	    		VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT);
		
		// div2 horizontal blur
		commandBuffer.pushConstantsCmd(horizontalBlurPipeline_div2.getLayoutHandle(),
				VK_SHADER_STAGE_COMPUTE_BIT, pushConstants_div2);
		commandBuffer.bindComputePipelineCmd(horizontalBlurPipeline_div2.getHandle());
		commandBuffer.bindComputeDescriptorSetsCmd(horizontalBlurPipeline_div2.getLayoutHandle(),
				VkUtil.createLongArray(horizontalBlurDescriptorSets_div2));
		commandBuffer.dispatchCmd(width/16, height/16, 1);
		// div4 horizontal blur
		commandBuffer.pushConstantsCmd(horizontalBlurPipeline_div4.getLayoutHandle(),
				VK_SHADER_STAGE_COMPUTE_BIT, pushConstants_div4);
		commandBuffer.bindComputePipelineCmd(horizontalBlurPipeline_div4.getHandle());
		commandBuffer.bindComputeDescriptorSetsCmd(horizontalBlurPipeline_div4.getLayoutHandle(),
				VkUtil.createLongArray(horizontalBlurDescriptorSets_div4));
		commandBuffer.dispatchCmd(width/32, height/32, 1);
		// div8 horizontal blur
		commandBuffer.pushConstantsCmd(horizontalBlurPipeline_div8.getLayoutHandle(),
				VK_SHADER_STAGE_COMPUTE_BIT, pushConstants_div8);
		commandBuffer.bindComputePipelineCmd(horizontalBlurPipeline_div8.getHandle());
		commandBuffer.bindComputeDescriptorSetsCmd(horizontalBlurPipeline_div8.getLayoutHandle(),
				VkUtil.createLongArray(horizontalBlurDescriptorSets_div8));
		commandBuffer.dispatchCmd(width/64, height/64, 1);
		// div16 horizontal blur
		commandBuffer.pushConstantsCmd(horizontalBlurPipeline_div16.getLayoutHandle(),
				VK_SHADER_STAGE_COMPUTE_BIT, pushConstants_div16);
		commandBuffer.bindComputePipelineCmd(horizontalBlurPipeline_div16.getHandle());
		commandBuffer.bindComputeDescriptorSetsCmd(horizontalBlurPipeline_div16.getLayoutHandle(),
				VkUtil.createLongArray(horizontalBlurDescriptorSets_div16));
		commandBuffer.dispatchCmd(width/128, height/128, 1);
		
		// barrier
		commandBuffer.pipelineMemoryBarrierCmd(
	    		VK_ACCESS_SHADER_WRITE_BIT, VK_ACCESS_SHADER_READ_BIT,
	    		VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT,
	    		VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT);
		
		// div2 vertical blur
		commandBuffer.pushConstantsCmd(verticalBlurPipeline_div2.getLayoutHandle(),
				VK_SHADER_STAGE_COMPUTE_BIT, pushConstants_div2);
		commandBuffer.bindComputePipelineCmd(verticalBlurPipeline_div2.getHandle());
		commandBuffer.bindComputeDescriptorSetsCmd(verticalBlurPipeline_div2.getLayoutHandle(),
				VkUtil.createLongArray(verticalBlurDescriptorSets_div2));
		commandBuffer.dispatchCmd(width/16, height/16, 1);
		// div4 horizontal blur
		commandBuffer.pushConstantsCmd(verticalBlurPipeline_div4.getLayoutHandle(),
				VK_SHADER_STAGE_COMPUTE_BIT, pushConstants_div4);
		commandBuffer.bindComputePipelineCmd(verticalBlurPipeline_div4.getHandle());
		commandBuffer.bindComputeDescriptorSetsCmd(verticalBlurPipeline_div4.getLayoutHandle(),
				VkUtil.createLongArray(verticalBlurDescriptorSets_div4));
		commandBuffer.dispatchCmd(width/32, height/32, 1);
		// div8 horizontal blur
		commandBuffer.pushConstantsCmd(verticalBlurPipeline_div8.getLayoutHandle(),
				VK_SHADER_STAGE_COMPUTE_BIT, pushConstants_div8);
		commandBuffer.bindComputePipelineCmd(verticalBlurPipeline_div8.getHandle());
		commandBuffer.bindComputeDescriptorSetsCmd(verticalBlurPipeline_div8.getLayoutHandle(),
				VkUtil.createLongArray(verticalBlurDescriptorSets_div8));
		commandBuffer.dispatchCmd(width/64, height/64, 1);
		// div16 horizontal blur
		commandBuffer.pushConstantsCmd(verticalBlurPipeline_div16.getLayoutHandle(),
				VK_SHADER_STAGE_COMPUTE_BIT, pushConstants_div16);
		commandBuffer.bindComputePipelineCmd(verticalBlurPipeline_div16.getHandle());
		commandBuffer.bindComputeDescriptorSetsCmd(verticalBlurPipeline_div16.getLayoutHandle(),
				VkUtil.createLongArray(verticalBlurDescriptorSets_div16));
		commandBuffer.dispatchCmd(width/128, height/128, 1);
		
		// barrier
		commandBuffer.pipelineMemoryBarrierCmd(
	    		VK_ACCESS_SHADER_WRITE_BIT, VK_ACCESS_SHADER_READ_BIT,
	    		VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT,
	    		VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT);
		
		// additive blend
		commandBuffer.pushConstantsCmd(blendPipeline.getLayoutHandle(),
				VK_SHADER_STAGE_COMPUTE_BIT, pushConstants_blend);
		commandBuffer.bindComputePipelineCmd(blendPipeline.getHandle());
		commandBuffer.bindComputeDescriptorSetsCmd(blendPipeline.getLayoutHandle(),
				VkUtil.createLongArray(blendDescriptorSets));
		commandBuffer.dispatchCmd(width/8, height/8, 1);
		
		// barrier
		commandBuffer.pipelineMemoryBarrierCmd(
	    		VK_ACCESS_SHADER_WRITE_BIT, VK_ACCESS_SHADER_READ_BIT,
	    		VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT,
	    		VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT);
		
		// bloom Scene
		commandBuffer.bindComputePipelineCmd(bloomScenePipeline.getHandle());
		commandBuffer.bindComputeDescriptorSetsCmd(bloomScenePipeline.getLayoutHandle(),
				VkUtil.createLongArray(bloomSceneDescriptorSets));
		commandBuffer.dispatchCmd(width/8, height/8, 1);
	}
	
	public void initializeImages(VkDevice device,
			VkPhysicalDeviceMemoryProperties memoryProperties, int width, int height){
		
		VkImage bloomSceneImage = new Image2DDeviceLocal(device, memoryProperties, 
				width, height, VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_USAGE_STORAGE_BIT
				| VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView bloomSceneImageView = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, bloomSceneImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		bloomSceneImageBundle = new VkImageBundle(bloomSceneImage, bloomSceneImageView);
		
		VkImage additiveBlendImage = new Image2DDeviceLocal(device, memoryProperties, 
				width, height, VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_USAGE_STORAGE_BIT
				| VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView additiveBlendImageView = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, additiveBlendImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		additiveBlendImageBundle = new VkImageBundle(additiveBlendImage, additiveBlendImageView);
		
		// brightness images
		
		VkImage sceneBrightnessImage = new Image2DDeviceLocal(device, memoryProperties, 
				width, height, VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_USAGE_STORAGE_BIT
				| VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView sceneBrightnessImageView = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, sceneBrightnessImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		VkSampler brightnessSampler = new VkSampler(device, VK_FILTER_LINEAR,
				false, 0, VK_SAMPLER_MIPMAP_MODE_LINEAR, 0, VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
		
		sceneBrightnessImageBundle = new VkImageBundle(sceneBrightnessImage, sceneBrightnessImageView,
				brightnessSampler);
		
		VkImage sceneBrightnessImage_div4 = new Image2DDeviceLocal(device, memoryProperties, 
				(int) (width/4.0f), (int) (height/4.0f), VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_USAGE_STORAGE_BIT
				| VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView sceneBrightnessImageView_div4 = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, sceneBrightnessImage_div4.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		VkSampler brightnessSampler_div4 = new VkSampler(device, VK_FILTER_LINEAR,
				false, 0, VK_SAMPLER_MIPMAP_MODE_LINEAR, 0, VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
		
		sceneBrightnessImageBundle_div4 = new VkImageBundle(sceneBrightnessImage_div4, sceneBrightnessImageView_div4,
				brightnessSampler_div4);
		
		VkImage sceneBrightnessImage_div8 = new Image2DDeviceLocal(device, memoryProperties, 
				(int) (width/8.0f), (int) (height/8.0f), VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_USAGE_STORAGE_BIT
				| VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView sceneBrightnessImageView_div8 = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, sceneBrightnessImage_div8.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		VkSampler brightnessSampler_div8 = new VkSampler(device, VK_FILTER_LINEAR,
				false, 0, VK_SAMPLER_MIPMAP_MODE_LINEAR, 0, VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
		
		sceneBrightnessImageBundle_div8 = new VkImageBundle(sceneBrightnessImage_div8, sceneBrightnessImageView_div8,
				brightnessSampler_div8);
		
		VkImage sceneBrightnessImage_div16 = new Image2DDeviceLocal(device, memoryProperties, 
				(int) (width/16.0f), (int) (height/16.0f), VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_USAGE_STORAGE_BIT
				| VK_IMAGE_USAGE_SAMPLED_BIT);
		VkImageView sceneBrightnessImageView_div16 = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, sceneBrightnessImage_div16.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		VkSampler brightnessSampler_div16 = new VkSampler(device, VK_FILTER_LINEAR,
				false, 0, VK_SAMPLER_MIPMAP_MODE_LINEAR, 0, VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
		
		sceneBrightnessImageBundle_div16 = new VkImageBundle(sceneBrightnessImage_div16, sceneBrightnessImageView_div16,
				brightnessSampler_div16);
		
		
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
		sceneBrightnessPipeline.destroy();
		sceneBrightnessDescriptorSet.destroy();
		sceneBrightnessDescriptorSetLayout.destroy();
		horizontalBlurPipeline_div2.destroy();
		horizontalBlurDescriptorSet_div2.destroy();
		horizontalBlurDescriptorSetLayout_div2.destroy();
		horizontalBlurPipeline_div4.destroy();
		horizontalBlurDescriptorSet_div4.destroy();
		horizontalBlurDescriptorSetLayout_div4.destroy();
		horizontalBlurPipeline_div8.destroy();
		horizontalBlurDescriptorSet_div8.destroy();
		horizontalBlurDescriptorSetLayout_div8.destroy();
		horizontalBlurPipeline_div16.destroy();
		horizontalBlurDescriptorSet_div16.destroy();
		horizontalBlurDescriptorSetLayout_div16.destroy();
		verticalBlurPipeline_div2.destroy();
		verticalBlurDescriptorSet_div2.destroy();
		verticalBlurDescriptorSetLayout_div2.destroy();
		verticalBlurPipeline_div4.destroy();
		verticalBlurDescriptorSet_div4.destroy();
		verticalBlurDescriptorSetLayout_div4.destroy();
		verticalBlurPipeline_div8.destroy();
		verticalBlurDescriptorSet_div8.destroy();
		verticalBlurDescriptorSetLayout_div8.destroy();
		verticalBlurPipeline_div16.destroy();
		verticalBlurDescriptorSet_div16.destroy();
		verticalBlurDescriptorSetLayout_div16.destroy();
		blendPipeline.destroy();
		blendDescriptorSet.destroy();
		blendDescriptorSetLayout.destroy();
		bloomBlurSampler_div2.destroy();
		bloomBlurSampler_div4.destroy();
		bloomBlurSampler_div8.destroy();
		bloomBlurSampler_div16.destroy();
		bloomScenePipeline.destroy();
		bloomSceneDescriptorSet.destroy();
		bloomSceneDescriptorSetLayout.destroy();
	}
	
	public VkImageView getBloomSceneImageView(){
		return bloomSceneImageBundle.getImageView();
	}
	
}
