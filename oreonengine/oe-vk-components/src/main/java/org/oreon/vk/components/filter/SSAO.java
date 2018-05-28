package org.oreon.vk.components.filter;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_STORAGE_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R16G16B16A16_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_GENERAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_STORAGE_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_COMPUTE_BIT;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.context.EngineContext;
import org.oreon.core.math.Quaternion;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.Util;
import org.oreon.core.vk.buffer.VkBuffer;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.SubmitInfo;
import org.oreon.core.vk.context.VkContext;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.image.VkImage;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.core.vk.pipeline.VkPipeline;
import org.oreon.core.vk.synchronization.Fence;
import org.oreon.core.vk.util.VkUtil;
import org.oreon.core.vk.wrapper.buffer.VkBufferHelper;
import org.oreon.core.vk.wrapper.command.ComputeCmdBuffer;
import org.oreon.core.vk.wrapper.image.Image2DDeviceLocal;
import org.oreon.core.vk.wrapper.shader.ComputeShader;

import lombok.Getter;

public class SSAO {

	private int kernelSize;
	private Quaternion[] kernel;
	private float[] randomx;
	private float[] randomy;
	
	private VkImageView noiseImageView;
	private VkImage noiseImage;
	private Fence fence;
	
	// ssao resources
	private VkPipeline ssaoPipeline;
	private VkImage ssaoImage;
	public static VkImageView ssaoImageView;
	private VkBuffer kernelBuffer;
	private DescriptorSet ssaoDescriptorSet;
	private DescriptorSetLayout ssaoDescriptorSetLayout;
	private CommandBuffer ssaoCmdBuffer;
	private SubmitInfo ssaoSubmitInfo;
	
	// ssao blur resources
	private VkImage ssaoBlurSceneImage;
	@Getter
	public static VkImageView ssaoBlurSceneImageView;
	private VkPipeline ssaoBlurPipeline;
	private DescriptorSet ssaoBlurDescriptorSet;
	private DescriptorSetLayout ssaoBlurDescriptorSetLayout;
	private CommandBuffer ssaoBlurCmdBuffer;
	private SubmitInfo ssaoBlurSubmitInfo;
	
	private final VkDevice device;
	private final VkPhysicalDeviceMemoryProperties memoryProperties;
	
	public SSAO(VkDevice device, VkPhysicalDeviceMemoryProperties memoryProperties,
			int width ,int height, VkImage worldPositionImage, 
			VkImageView worldPositionImageView,
			VkImageView normalImageView) {
		
		this.device = device;
		this.memoryProperties = memoryProperties;
		
		kernelSize = 64;
		
		randomx = new float[16];
		randomy = new float[16];
		
		for (int i=0; i<16; i++){
			randomx[i] = (float) Math.random() * 2 - 1;
			randomy[i] = (float) Math.random() * 2 - 1;
		}
	
		kernel = Util.generateRandomKernel4D(kernelSize);
		
		generateNoise();
		
		ssaoImage = new Image2DDeviceLocal(device, memoryProperties, 
				width, height, VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_USAGE_STORAGE_BIT
				| VK_IMAGE_USAGE_SAMPLED_BIT);
		ssaoImageView = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, ssaoImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		// ssao resources
		int pushConstantRange = Float.BYTES * 21;
		ByteBuffer pushConstants = memAlloc(pushConstantRange);
		pushConstants.put(BufferUtil.createByteBuffer(EngineContext.getCamera().getProjectionMatrix()));
		pushConstants.putFloat(1f);
		pushConstants.putFloat(0.01f);
		pushConstants.putFloat(kernelSize);
		pushConstants.putFloat(width);
		pushConstants.putFloat(height);
		pushConstants.flip();
		
		kernelBuffer = VkBufferHelper.createDeviceLocalBuffer(device,
				memoryProperties,
        		VkContext.getLogicalDevice().getTransferCommandPool().getHandle(),
        		VkContext.getLogicalDevice().getTransferQueue(),
        		BufferUtil.createByteBuffer(kernel),
        		VK_BUFFER_USAGE_STORAGE_BUFFER_BIT);
		
		ssaoDescriptorSetLayout = new DescriptorSetLayout(device, 5);
		ssaoDescriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		ssaoDescriptorSetLayout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		ssaoDescriptorSetLayout.addLayoutBinding(2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		ssaoDescriptorSetLayout.addLayoutBinding(3, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		ssaoDescriptorSetLayout.addLayoutBinding(4, VK_DESCRIPTOR_TYPE_STORAGE_BUFFER,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		ssaoDescriptorSetLayout.create();

		ssaoDescriptorSet = new DescriptorSet(device,
		    		VkContext.getDescriptorPoolManager().getDescriptorPool("POOL_1").getHandle(),
		    		ssaoDescriptorSetLayout.getHandlePointer());
		ssaoDescriptorSet.updateDescriptorImageBuffer(ssaoImageView.getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, -1, 0,
		    	VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		ssaoDescriptorSet.updateDescriptorImageBuffer(worldPositionImageView.getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, -1, 1,
	    		VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		ssaoDescriptorSet.updateDescriptorImageBuffer(normalImageView.getHandle(),
	    		VK_IMAGE_LAYOUT_GENERAL, -1, 2,
	    		VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		ssaoDescriptorSet.updateDescriptorImageBuffer(noiseImageView.getHandle(),
	    		VK_IMAGE_LAYOUT_GENERAL, -1, 3,
	    		VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		ssaoDescriptorSet.updateDescriptorBuffer(kernelBuffer.getHandle(),
				Float.BYTES * 3 * kernelSize, 0, 4,
	    		VK_DESCRIPTOR_TYPE_STORAGE_BUFFER);
		
		List<DescriptorSet> descriptorSets = new ArrayList<DescriptorSet>();
		List<DescriptorSetLayout> descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		descriptorSets.add(VkContext.getCamera().getDescriptor().getSet());
		descriptorSets.add(ssaoDescriptorSet);
		descriptorSetLayouts.add(VkContext.getCamera().getDescriptor().getLayout());
		descriptorSetLayouts.add(ssaoDescriptorSetLayout);
		
		ssaoPipeline = new VkPipeline(device);
		ssaoPipeline.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		ssaoPipeline.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		ssaoPipeline.createComputePipeline(new ComputeShader(device, "shaders/filter/ssao/ssao.comp.spv"));
		
		ssaoCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				ssaoPipeline.getHandle(), ssaoPipeline.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets), width/16, height/16, 1,
				pushConstants, VK_SHADER_STAGE_COMPUTE_BIT);
		
		fence = new Fence(device);
		
		ssaoSubmitInfo = new SubmitInfo();
		ssaoSubmitInfo.setCommandBuffers(ssaoCmdBuffer.getHandlePointer());
		ssaoSubmitInfo.setFence(fence);
		
		// ssao blur resources
		ssaoBlurSceneImage = new Image2DDeviceLocal(device, memoryProperties, 
				width, height, VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_USAGE_STORAGE_BIT
				| VK_IMAGE_USAGE_SAMPLED_BIT);
		ssaoBlurSceneImageView = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, ssaoBlurSceneImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		ssaoBlurDescriptorSetLayout = new DescriptorSetLayout(device, 2);
		ssaoBlurDescriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		ssaoBlurDescriptorSetLayout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		ssaoBlurDescriptorSetLayout.create();

		ssaoBlurDescriptorSet = new DescriptorSet(device,
		    		VkContext.getDescriptorPoolManager().getDescriptorPool("POOL_1").getHandle(),
		    		ssaoBlurDescriptorSetLayout.getHandlePointer());
		ssaoBlurDescriptorSet.updateDescriptorImageBuffer(ssaoBlurSceneImageView.getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, -1, 0,
		    	VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		ssaoBlurDescriptorSet.updateDescriptorImageBuffer(ssaoImageView.getHandle(),
				VK_IMAGE_LAYOUT_GENERAL, -1, 1,
	    		VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		
		descriptorSets = new ArrayList<DescriptorSet>();
		descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		descriptorSets.add(ssaoBlurDescriptorSet);
		descriptorSetLayouts.add(ssaoBlurDescriptorSetLayout);
		
		ssaoBlurPipeline = new VkPipeline(device);
		ssaoBlurPipeline.setLayout(VkUtil.createLongBuffer(descriptorSetLayouts));
		ssaoBlurPipeline.createComputePipeline(new ComputeShader(device, "shaders/filter/ssao/ssaoBlur.comp.spv"));
		
		ssaoBlurCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				ssaoBlurPipeline.getHandle(), ssaoBlurPipeline.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSets), width/16, height/16, 1);
		
		fence = new Fence(device);
		
		ssaoBlurSubmitInfo = new SubmitInfo();
		ssaoBlurSubmitInfo.setCommandBuffers(ssaoBlurCmdBuffer.getHandlePointer());
		ssaoBlurSubmitInfo.setFence(fence);
	}
	
	public void render(){
		
		ssaoSubmitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());
//		ssaoBlurSubmitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());
	}
	
	private void generateNoise(){
		
		noiseImage = new Image2DDeviceLocal(device, memoryProperties, 
				4, 4, VK_FORMAT_R16G16B16A16_SFLOAT, VK_IMAGE_USAGE_STORAGE_BIT);
		noiseImageView = new VkImageView(device,
				VK_FORMAT_R16G16B16A16_SFLOAT, noiseImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		int pushConstantRange = Float.BYTES * 32;
		
		ByteBuffer pushConstants = memAlloc(pushConstantRange);
		for (int i=0; i<randomx.length; i++){
			pushConstants.putFloat(randomx[i]);
		}
		for (int i=0; i<randomy.length; i++){
			pushConstants.putFloat(randomy[i]);
		}
		pushConstants.flip();
		
		DescriptorSetLayout descriptorSetLayout = new DescriptorSetLayout(device, 1);
		descriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
	    		VK_SHADER_STAGE_COMPUTE_BIT);
		descriptorSetLayout.create();
		DescriptorSet descriptorSet = new DescriptorSet(device,
		    		VkContext.getDescriptorPoolManager().getDescriptorPool("POOL_1").getHandle(),
		    		descriptorSetLayout.getHandlePointer());
		descriptorSet.updateDescriptorImageBuffer(noiseImageView.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		
		VkPipeline pipeline = new VkPipeline(device);
		pipeline.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, pushConstantRange);
		pipeline.setLayout(descriptorSetLayout.getHandlePointer());
		pipeline.createComputePipeline(new ComputeShader(device, "shaders/filter/ssao/noise.comp.spv"));
		
		CommandBuffer commandBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				pipeline.getHandle(), pipeline.getLayoutHandle(),
				VkUtil.createLongArray(descriptorSet), 1, 1, 1,
				pushConstants, VK_SHADER_STAGE_COMPUTE_BIT);
		
		Fence fence = new Fence(device);
		
		SubmitInfo submitInfo = new SubmitInfo();
		submitInfo.setCommandBuffers(commandBuffer.getHandlePointer());
		submitInfo.setFence(fence);
		submitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());
		
		fence.waitForFence();
	}
	
}
