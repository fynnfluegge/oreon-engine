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
import java.nio.IntBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.core.math.Vec2f;
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
import org.oreon.core.vk.wrapper.image.Image2DLocal;

import lombok.Getter;

public class FFT extends Renderable{

	@Getter
	private VkImageView dxImageView;
	@Getter
	private VkImageView dyImageView;
	@Getter
	private VkImageView dzImageView;
	
	private int pingpong;
	private int stages;
	
	private VkImage dxImage;
	private VkImage dyImage;
	private VkImage dzImage;
	
	private TwiddleFactors twiddleFactors;
	private H0k h0k;
	private Hkt hkt;
	
	private DescriptorSetLayout descriptorLayout;
	private VkPipeline butterflyPipeline;
	private VkPipeline inversionPipeline;
	private ShaderModule butterflyShader;
	private ShaderModule inversionShader;
	
	// dy fft resources
	private DescriptorSet dyButterflyDescriptorSet;
	private DescriptorSet dyInversionDescriptorSet;
	private CommandBuffer dyButterflyCmdBuffer;
	private CommandBuffer dyInversionCmdBuffer;
	private VkImage dyPingpongImage;
	private VkImageView dyPingpongImageView;
	
	// dx fft resources
	private DescriptorSet dxButterflyDescriptorSet;
	private DescriptorSet dxInversionDescriptorSet;
	private CommandBuffer dxButterflyCmdBuffer;
	private CommandBuffer dxInversionCmdBuffer;
	private VkImage dxPingpongImage;
	private VkImageView dxPingpongImageView;
	
	// dz fft resources
	private DescriptorSet dzButterflyDescriptorSet;
	private DescriptorSet dzInversionDescriptorSet;
	private CommandBuffer dzButterflyCmdBuffer;
	private CommandBuffer dzInversionCmdBuffer;
	private VkImage dzPingpongImage;
	private VkImageView dzPingpongImageView;
	
	private VkUniformBuffer buffer;
	
	private SubmitInfo dySubmitInfo;
	private SubmitInfo dxSubmitInfo;
	private SubmitInfo dzSubmitInfo;
	private SubmitInfo inversionSubmitInfo;
	
	private Fence dyFence;
	private Fence dxFence;
	private Fence dzFence;
	
	private class ButterflyDescriptorSet extends DescriptorSet{
		
		public ButterflyDescriptorSet(VkDevice device,
				DescriptorSetLayout layout, VkImageView twiddleFactors,
				VkImageView coefficients, VkImageView pingpongImage) {
		    
		    super(device, VkContext.getDescriptorPoolManager().getDescriptorPool("POOL_1").getHandle(),
		    		layout.getHandlePointer());
		    
		    updateDescriptorImageBuffer(twiddleFactors.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    updateDescriptorImageBuffer(coefficients.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    updateDescriptorImageBuffer(pingpongImage.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    updateDescriptorBuffer(buffer.getHandle(),
		    		Integer.BYTES * 3, 0, 3, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
		}
	}
	
	private class InversionDescriptorSet extends DescriptorSet{
		
		public InversionDescriptorSet(VkDevice device,
				DescriptorSetLayout layout, VkImageView spatialDomain,
				VkImageView coefficients, VkImageView pingpongImage) {
		    
		    super(device,
		    		VkContext.getDescriptorPoolManager().getDescriptorPool("POOL_1").getHandle(),
		    		layout.getHandlePointer());
		    updateDescriptorImageBuffer(spatialDomain.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    updateDescriptorImageBuffer(coefficients.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    updateDescriptorImageBuffer(pingpongImage.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    updateDescriptorBuffer(buffer.getHandle(),
		    		Integer.BYTES * 3, 0, 3, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
		}
	}

	public FFT(VkDevice device,
			VkPhysicalDeviceMemoryProperties memoryProperties,int N, int L,
			float amplitude, Vec2f direction, float intensity, float capillarSupressFactor) {
		
		stages =  (int) (Math.log(N)/Math.log(2));
		
		dyPingpongImage = new Image2DLocal(device, memoryProperties, N, N,
				VK_FORMAT_R32G32B32A32_SFLOAT,
				VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);
		
		dyPingpongImageView = new VkImageView(device,
				VK_FORMAT_R32G32B32A32_SFLOAT, dyPingpongImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		dxPingpongImage = new Image2DLocal(device, memoryProperties, N, N,
				VK_FORMAT_R32G32B32A32_SFLOAT,
				VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);
		
		dxPingpongImageView = new VkImageView(device,
				VK_FORMAT_R32G32B32A32_SFLOAT, dxPingpongImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		dzPingpongImage = new Image2DLocal(device, memoryProperties, N, N,
				VK_FORMAT_R32G32B32A32_SFLOAT,
				VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);
		
		dzPingpongImageView = new VkImageView(device,
				VK_FORMAT_R32G32B32A32_SFLOAT, dzPingpongImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		dyImage = new Image2DLocal(device, memoryProperties, N, N,
				VK_FORMAT_R32G32B32A32_SFLOAT,
				VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);
		
		dyImageView = new VkImageView(device,
				VK_FORMAT_R32G32B32A32_SFLOAT, dyImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		dxImage = new Image2DLocal(device, memoryProperties, N, N,
				VK_FORMAT_R32G32B32A32_SFLOAT,
				VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);
		
		dxImageView = new VkImageView(device,
				VK_FORMAT_R32G32B32A32_SFLOAT, dxImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		dzImage = new Image2DLocal(device, memoryProperties, N, N,
				VK_FORMAT_R32G32B32A32_SFLOAT,
				VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT);
		
		dzImageView = new VkImageView(device,
				VK_FORMAT_R32G32B32A32_SFLOAT, dzImage.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		twiddleFactors = new TwiddleFactors(
				VkContext.getLogicalDevice().getHandle(),
				VkContext.getPhysicalDevice().getMemoryProperties(), N);
		
		h0k = new H0k(VkContext.getLogicalDevice().getHandle(),
				VkContext.getPhysicalDevice().getMemoryProperties(), N, L,
				amplitude, direction, intensity, capillarSupressFactor);
		
		hkt = new Hkt(VkContext.getLogicalDevice().getHandle(),
				VkContext.getPhysicalDevice().getMemoryProperties(),
				N, L, h0k.getH0k_imageView(), h0k.getH0minusk_imageView());
		
		ByteBuffer ubo = memAlloc(Integer.BYTES * 3);
		ubo.putInt(0);
		ubo.putInt(0);
		ubo.putInt(0);
		ubo.flip();
		
		ByteBuffer pushConstants = memAlloc(Integer.BYTES * 1);
		IntBuffer intBuffer = pushConstants.asIntBuffer();
		intBuffer.put(N);
		
		buffer = new VkUniformBuffer(VkContext.getLogicalDevice().getHandle(),
				VkContext.getPhysicalDevice().getMemoryProperties(),ubo);
		
		descriptorLayout = new DescriptorSetLayout(device, 4);
		descriptorLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
		    		VK_SHADER_STAGE_COMPUTE_BIT);
		descriptorLayout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
		    		VK_SHADER_STAGE_COMPUTE_BIT);
		descriptorLayout.addLayoutBinding(2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE,
		    		VK_SHADER_STAGE_COMPUTE_BIT);
		descriptorLayout.addLayoutBinding(3,VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
		    		VK_SHADER_STAGE_COMPUTE_BIT);
		descriptorLayout.create();
	
		dyButterflyDescriptorSet = new ButterflyDescriptorSet(device,
				descriptorLayout, twiddleFactors.getImageView(),
				hkt.getDyCoefficients_imageView(),
				dyPingpongImageView);
		dyInversionDescriptorSet = new InversionDescriptorSet(device,
				descriptorLayout, dyImageView, hkt.getDyCoefficients_imageView(),
				dyPingpongImageView);
		
		dxButterflyDescriptorSet = new ButterflyDescriptorSet(device,
				descriptorLayout, twiddleFactors.getImageView(),
				hkt.getDxCoefficients_imageView(),
				dxPingpongImageView);
		dxInversionDescriptorSet = new InversionDescriptorSet(device,
				descriptorLayout, dxImageView, hkt.getDxCoefficients_imageView(),
				dxPingpongImageView);
		
		dzButterflyDescriptorSet = new ButterflyDescriptorSet(device,
				descriptorLayout, twiddleFactors.getImageView(),
				hkt.getDzCoefficients_imageView(),
				dzPingpongImageView);
		dzInversionDescriptorSet = new InversionDescriptorSet(device,
				descriptorLayout, dzImageView, hkt.getDzCoefficients_imageView(),
				dzPingpongImageView);
		
		butterflyShader = new ShaderModule(device, "shaders/fft/Butterfly.comp.spv", VK_SHADER_STAGE_COMPUTE_BIT);
		inversionShader = new ShaderModule(device, "shaders/fft/Inversion.comp.spv", VK_SHADER_STAGE_COMPUTE_BIT);
		
		butterflyPipeline = new VkPipeline(device);
		butterflyPipeline.setLayout(descriptorLayout.getHandlePointer());
		butterflyPipeline.createComputePipeline(butterflyShader);
		
		inversionPipeline = new VkPipeline(device);
		inversionPipeline.setPushConstantsRange(VK_SHADER_STAGE_COMPUTE_BIT, Integer.BYTES * 1);
		inversionPipeline.setLayout(descriptorLayout.getHandlePointer());
		inversionPipeline.createComputePipeline(inversionShader);
		
		dyButterflyCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				butterflyPipeline.getHandle(), butterflyPipeline.getLayoutHandle(),
				VkUtil.createLongArray(dyButterflyDescriptorSet), N/16, N/16, 1);
		
		dxButterflyCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				butterflyPipeline.getHandle(), butterflyPipeline.getLayoutHandle(),
				VkUtil.createLongArray(dxButterflyDescriptorSet), N/16, N/16, 1);
		
		dzButterflyCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				butterflyPipeline.getHandle(), butterflyPipeline.getLayoutHandle(),
				VkUtil.createLongArray(dzButterflyDescriptorSet), N/16, N/16, 1);
		
		dyInversionCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				inversionPipeline.getHandle(), inversionPipeline.getLayoutHandle(),
				VkUtil.createLongArray(dyInversionDescriptorSet), N/16, N/16, 1,
				pushConstants, VK_SHADER_STAGE_COMPUTE_BIT);
		
		dxInversionCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				inversionPipeline.getHandle(), inversionPipeline.getLayoutHandle(),
				VkUtil.createLongArray(dxInversionDescriptorSet), N/16, N/16, 1,
				pushConstants, VK_SHADER_STAGE_COMPUTE_BIT);
		
		dzInversionCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				inversionPipeline.getHandle(), inversionPipeline.getLayoutHandle(),
				VkUtil.createLongArray(dzInversionDescriptorSet), N/16, N/16, 1,
				pushConstants, VK_SHADER_STAGE_COMPUTE_BIT);
		
		dyFence = new Fence(device);
		dxFence = new Fence(device);
		dzFence = new Fence(device);
		
		dySubmitInfo = new SubmitInfo();
		dySubmitInfo.setFence(dyFence);
		dySubmitInfo.setCommandBuffers(dyButterflyCmdBuffer.getHandlePointer());
		
		dxSubmitInfo = new SubmitInfo();
		dxSubmitInfo.setFence(dxFence);
		dxSubmitInfo.setCommandBuffers(dxButterflyCmdBuffer.getHandlePointer());
		
		dzSubmitInfo = new SubmitInfo();
		dzSubmitInfo.setFence(dzFence);
		dzSubmitInfo.setCommandBuffers(dzButterflyCmdBuffer.getHandlePointer());
		
		inversionSubmitInfo = new SubmitInfo();
	}
	
	public void render(){
		
		hkt.render();
		hkt.getFence().waitForFence();
		
		pingpong = 0;

		// Dy-FFT
		// 1D FFT horizontal 
		for (int i=0; i<stages; i++)
		{
			int[] uniforms = {i, pingpong, 0};
			buffer.mapMemory(BufferUtil.createByteBuffer(uniforms));
			dySubmitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());
			dxSubmitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());
			dzSubmitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());
			
			dyFence.waitForFence();
			dxFence.waitForFence();
			dzFence.waitForFence();
			
			pingpong++;
			pingpong %= 2;
		}
		
		//1D FFT vertical 
		for (int j=0; j<stages; j++)
		{
			int[] uniforms = {j, pingpong, 1};
			buffer.mapMemory(BufferUtil.createByteBuffer(uniforms));
			dySubmitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());
			dxSubmitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());
			dzSubmitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());
			
			dyFence.waitForFence();
			dxFence.waitForFence();
			dzFence.waitForFence();
			
			pingpong++;
			pingpong %= 2;
		}
		
		int[] inversionUniforms = {0, pingpong, 1};
		buffer.mapMemory(BufferUtil.createByteBuffer(inversionUniforms));
		
		inversionSubmitInfo.setCommandBuffers(dyInversionCmdBuffer.getHandlePointer());
		inversionSubmitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());

		inversionSubmitInfo.setCommandBuffers(dxInversionCmdBuffer.getHandlePointer());
		inversionSubmitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());
		
		inversionSubmitInfo.setCommandBuffers(dzInversionCmdBuffer.getHandlePointer());
		inversionSubmitInfo.submit(VkContext.getLogicalDevice().getComputeQueue());
	}
	
	public void destroy(){
		
		twiddleFactors.destroy();
		h0k.destroy();
		hkt.shutdown();
		buffer.destroy();
	}
}
