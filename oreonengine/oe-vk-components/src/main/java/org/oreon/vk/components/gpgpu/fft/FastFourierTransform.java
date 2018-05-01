package org.oreon.vk.components.gpgpu.fft;

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
import org.oreon.core.vk.wrapper.buffer.VkUniformBuffer;
import org.oreon.core.vk.wrapper.command.ComputeCmdBuffer;
import org.oreon.core.vk.wrapper.image.Image2DLocal;

import lombok.Getter;

public class FastFourierTransform extends Renderable{

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
	private Spectrum spectrum;
	private Coefficients coefficients;
	
	private DescriptorSetLayout descriptorLayout;
	private VkPipeline butterflyPipeline;
	private VkPipeline inversionPipeline;
	private ShaderModule butterflyShader;
	private ShaderModule inversionShader;
	
	// dy fft resources
	private Descriptor dyButterflyDescriptor;
	private Descriptor dyInversionDescriptor;
	private CommandBuffer dyButterflyCmdBuffer;
	private CommandBuffer dyInversionCmdBuffer;
	private VkImage dyPingpongImage;
	private VkImageView dyPingpongImageView;
	
	// dx fft resources
	private Descriptor dxButterflyDescriptor;
	private Descriptor dxInversionDescriptor;
	private CommandBuffer dxButterflyCmdBuffer;
	private CommandBuffer dxInversionCmdBuffer;
	private VkImage dxPingpongImage;
	private VkImageView dxPingpongImageView;
	
	// dz fft resources
	private Descriptor dzButterflyDescriptor;
	private Descriptor dzInversionDescriptor;
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
	
	private class FastFourierTransformButterflyDescriptor extends Descriptor{
		
		public FastFourierTransformButterflyDescriptor(VkDevice device,
				DescriptorSetLayout layout, VkImageView twiddleFactors,
				VkImageView coefficients, VkImageView pingpongImage) {
		    
		    set = new DescriptorSet(device,
		    		VkContext.getDescriptorPoolManager().getDescriptorPool("POOL_1").getHandle(),
		    		layout.getHandlePointer());
		    set.updateDescriptorImageBuffer(twiddleFactors.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    set.updateDescriptorImageBuffer(coefficients.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    set.updateDescriptorImageBuffer(pingpongImage.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    set.updateDescriptorBuffer(buffer.getHandle(),
		    		Integer.BYTES * 3, 0, 3, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
		}
	}
	
	private class FastFourierTransformInversionDescriptor extends Descriptor{
		
		public FastFourierTransformInversionDescriptor(VkDevice device,
				DescriptorSetLayout layout, VkImageView spatialDomain,
				VkImageView coefficients, VkImageView pingpongImage) {
		    
		    set = new DescriptorSet(device,
		    		VkContext.getDescriptorPoolManager().getDescriptorPool("POOL_1").getHandle(),
		    		layout.getHandlePointer());
		    set.updateDescriptorImageBuffer(spatialDomain.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    set.updateDescriptorImageBuffer(coefficients.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		1, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    set.updateDescriptorImageBuffer(pingpongImage.getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		2, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
		    set.updateDescriptorBuffer(buffer.getHandle(),
		    		Integer.BYTES * 3, 0, 3, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
		}
	}

	public FastFourierTransform(VkDevice device,
			VkPhysicalDeviceMemoryProperties memoryProperties,int N, int L) {
		
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
		
		spectrum = new Spectrum(VkContext.getLogicalDevice().getHandle(),
				VkContext.getPhysicalDevice().getMemoryProperties(), N, L,
				20, new Vec2f(1,1), 25, 1);
		
		coefficients = new Coefficients(VkContext.getLogicalDevice().getHandle(),
				VkContext.getPhysicalDevice().getMemoryProperties(),
				N, L, spectrum.getH0k_imageView(), spectrum.getH0minusk_imageView());
		
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
	
		dyButterflyDescriptor = new FastFourierTransformButterflyDescriptor(device,
				descriptorLayout, twiddleFactors.getImageView(),
				coefficients.getDyCoefficients_imageView(),
				dyPingpongImageView);
		dyInversionDescriptor = new FastFourierTransformInversionDescriptor(device,
				descriptorLayout, dyImageView, coefficients.getDyCoefficients_imageView(),
				dyPingpongImageView);
		
		dxButterflyDescriptor = new FastFourierTransformButterflyDescriptor(device,
				descriptorLayout, twiddleFactors.getImageView(),
				coefficients.getDxCoefficients_imageView(),
				dxPingpongImageView);
		dxInversionDescriptor = new FastFourierTransformInversionDescriptor(device,
				descriptorLayout, dxImageView, coefficients.getDxCoefficients_imageView(),
				dxPingpongImageView);
		
		dzButterflyDescriptor = new FastFourierTransformButterflyDescriptor(device,
				descriptorLayout, twiddleFactors.getImageView(),
				coefficients.getDzCoefficients_imageView(),
				dzPingpongImageView);
		dzInversionDescriptor = new FastFourierTransformInversionDescriptor(device,
				descriptorLayout, dzImageView, coefficients.getDzCoefficients_imageView(),
				dzPingpongImageView);
		
		butterflyShader = new ShaderModule(device, "fft/Butterfly.comp.spv", VK_SHADER_STAGE_COMPUTE_BIT);
		inversionShader = new ShaderModule(device, "fft/Inversion.comp.spv", VK_SHADER_STAGE_COMPUTE_BIT);
		
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
				VkUtil.createLongArray(dyButterflyDescriptor.getSet()), N/16, N/16, 1);
		
		dxButterflyCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				butterflyPipeline.getHandle(), butterflyPipeline.getLayoutHandle(),
				VkUtil.createLongArray(dxButterflyDescriptor.getSet()), N/16, N/16, 1);
		
		dzButterflyCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				butterflyPipeline.getHandle(), butterflyPipeline.getLayoutHandle(),
				VkUtil.createLongArray(dzButterflyDescriptor.getSet()), N/16, N/16, 1);
		
		dyInversionCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				inversionPipeline.getHandle(), inversionPipeline.getLayoutHandle(),
				VkUtil.createLongArray(dyInversionDescriptor.getSet()), N/16, N/16, 1,
				pushConstants, VK_SHADER_STAGE_COMPUTE_BIT);
		
		dxInversionCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				inversionPipeline.getHandle(), inversionPipeline.getLayoutHandle(),
				VkUtil.createLongArray(dxInversionDescriptor.getSet()), N/16, N/16, 1,
				pushConstants, VK_SHADER_STAGE_COMPUTE_BIT);
		
		dzInversionCmdBuffer = new ComputeCmdBuffer(device,
				VkContext.getLogicalDevice().getComputeCommandPool().getHandle(),
				inversionPipeline.getHandle(), inversionPipeline.getLayoutHandle(),
				VkUtil.createLongArray(dzInversionDescriptor.getSet()), N/16, N/16, 1,
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
		
		coefficients.render();
		coefficients.getFence().waitForFence();
		
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
			
			dxButterflyDescriptor.getSet().updateDescriptorImageBuffer(
					twiddleFactors.getImageView().getHandle(),
		    		VK_IMAGE_LAYOUT_GENERAL, -1,
		    		0, VK_DESCRIPTOR_TYPE_STORAGE_IMAGE);
			
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
		spectrum.destroy();
		coefficients.shutdown();
		buffer.destroy();
	}
}
