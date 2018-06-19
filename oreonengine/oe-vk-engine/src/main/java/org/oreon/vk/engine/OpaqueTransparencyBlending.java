package org.oreon.vk.engine;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_MEMORY_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_LOAD_OP_CLEAR;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_DEPENDENCY_BY_REGION_BIT;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_FILTER_LINEAR;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R16G16B16A16_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_GENERAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;
import static org.lwjgl.vulkan.VK10.VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT;
import static org.lwjgl.vulkan.VK10.VK_SAMPLER_MIPMAP_MODE_LINEAR;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SUBPASS_EXTERNAL;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkQueue;
import org.oreon.core.context.EngineContext;
import org.oreon.core.model.Mesh;
import org.oreon.core.model.Vertex.VertexLayout;
import org.oreon.core.target.FrameBufferObject.Attachment;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.MeshGenerator;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.SubmitInfo;
import org.oreon.core.vk.descriptor.DescriptorPool;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.device.VkDeviceBundle;
import org.oreon.core.vk.framebuffer.FrameBufferColorAttachment;
import org.oreon.core.vk.framebuffer.VkFrameBuffer;
import org.oreon.core.vk.framebuffer.VkFrameBufferObject;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.core.vk.image.VkSampler;
import org.oreon.core.vk.memory.VkBuffer;
import org.oreon.core.vk.pipeline.RenderPass;
import org.oreon.core.vk.pipeline.ShaderPipeline;
import org.oreon.core.vk.pipeline.VkVertexInput;
import org.oreon.core.vk.util.VkUtil;
import org.oreon.core.vk.wrapper.buffer.VkBufferHelper;
import org.oreon.core.vk.wrapper.command.DrawCmdBuffer;
import org.oreon.core.vk.wrapper.image.VkImageBundle;
import org.oreon.core.vk.wrapper.pipeline.GraphicsPipeline;

public class OpaqueTransparencyBlending {
	
	private VkQueue queue;
	
	private VkFrameBufferObject fbo;
	private GraphicsPipeline graphicsPipeline;
	private DescriptorSet descriptorSet;
	private DescriptorSetLayout descriptorSetLayout;
	private CommandBuffer cmdBuffer;
	private SubmitInfo submitInfo;
	
	// sampler
	private VkSampler opaqueSceneSampler;
	private VkSampler opaqueSceneDepthSampler;
	private VkSampler opaqueSceneLightScatteringSampler;
	private VkSampler transparencySceneSampler;
	private VkSampler transparencySceneDepthSampler;
	private VkSampler transparencyAlphaSampler;
	private VkSampler transparencyLightScatteringSampler;

	public OpaqueTransparencyBlending(VkDeviceBundle deviceBundle,
			int width ,int height, VkImageView opaqueSceneImageView,
			VkImageView opaqueSceneDepthMap, VkImageView opaqueSceneLightScatteringImageView,
			VkImageView transparencySceneImageView, VkImageView transparencySceneDepthMap,
			VkImageView transparencyAlphaMap, VkImageView transparencyLightScatteringImageView) {
		
		VkDevice device = deviceBundle.getLogicalDevice().getHandle();
		VkPhysicalDeviceMemoryProperties memoryProperties = deviceBundle.getPhysicalDevice().getMemoryProperties();
		DescriptorPool descriptorPool = deviceBundle.getLogicalDevice().getDescriptorPool(Thread.currentThread().getId());
		queue = deviceBundle.getLogicalDevice().getGraphicsQueue();
		
		fbo = new OpaqueTransparencyBlendFbo(device, memoryProperties);
		
		Mesh mesh = MeshGenerator.NDCQuad2D();
		VkVertexInput vertexInput = new VkVertexInput(VertexLayout.POS_UV);
		ByteBuffer vertexBuffer = BufferUtil.createByteBuffer(mesh.getVertices(), VertexLayout.POS_UV);
		ByteBuffer indexBuffer = BufferUtil.createByteBuffer(mesh.getIndices());
		
		VkBuffer vertexBufferObject = VkBufferHelper.createDeviceLocalBuffer(
				device, memoryProperties,
				deviceBundle.getLogicalDevice().getTransferCommandPool().getHandle(),
				deviceBundle.getLogicalDevice().getTransferQueue(),
				vertexBuffer, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);

        VkBuffer indexBufferObject = VkBufferHelper.createDeviceLocalBuffer(
        		device, memoryProperties,
        		deviceBundle.getLogicalDevice().getTransferCommandPool().getHandle(),
				deviceBundle.getLogicalDevice().getTransferQueue(),
        		indexBuffer, VK_BUFFER_USAGE_INDEX_BUFFER_BIT);
		
		ShaderPipeline graphicsShaderPipeline = new ShaderPipeline(device);
	    graphicsShaderPipeline.createVertexShader("shaders/quad.vert.spv");
	    graphicsShaderPipeline.createFragmentShader("shaders/opaqueTransparencyBlend.frag.spv");
	    graphicsShaderPipeline.createShaderPipeline();
		
	    opaqueSceneSampler = new VkSampler(device, VK_FILTER_LINEAR,
				false, 0, VK_SAMPLER_MIPMAP_MODE_LINEAR, 0, VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT);
	    opaqueSceneDepthSampler = new VkSampler(device, VK_FILTER_LINEAR,
				false, 0, VK_SAMPLER_MIPMAP_MODE_LINEAR, 0, VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT);
	    opaqueSceneLightScatteringSampler = new VkSampler(device, VK_FILTER_LINEAR,
				false, 0, VK_SAMPLER_MIPMAP_MODE_LINEAR, 0, VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT);
	    transparencySceneSampler = new VkSampler(device, VK_FILTER_LINEAR,
				false, 0, VK_SAMPLER_MIPMAP_MODE_LINEAR, 0, VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT);
	    transparencySceneDepthSampler = new VkSampler(device, VK_FILTER_LINEAR,
				false, 0, VK_SAMPLER_MIPMAP_MODE_LINEAR, 0, VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT);
	    transparencyAlphaSampler = new VkSampler(device, VK_FILTER_LINEAR,
				false, 0, VK_SAMPLER_MIPMAP_MODE_LINEAR, 0, VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT);
	    transparencyLightScatteringSampler = new VkSampler(device, VK_FILTER_LINEAR,
				false, 0, VK_SAMPLER_MIPMAP_MODE_LINEAR, 0, VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT);
		
	    descriptorSetLayout = new DescriptorSetLayout(device,7);
		descriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
				VK_SHADER_STAGE_FRAGMENT_BIT);
		descriptorSetLayout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
				VK_SHADER_STAGE_FRAGMENT_BIT);
		descriptorSetLayout.addLayoutBinding(2, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
				VK_SHADER_STAGE_FRAGMENT_BIT);
		descriptorSetLayout.addLayoutBinding(3, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
				VK_SHADER_STAGE_FRAGMENT_BIT);
		descriptorSetLayout.addLayoutBinding(4, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
				VK_SHADER_STAGE_FRAGMENT_BIT);
		descriptorSetLayout.addLayoutBinding(5, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
				VK_SHADER_STAGE_FRAGMENT_BIT);
		descriptorSetLayout.addLayoutBinding(6, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
				VK_SHADER_STAGE_FRAGMENT_BIT);
	    descriptorSetLayout.create();
	    
	    descriptorSet = new DescriptorSet(device, descriptorPool.getHandle(),
	    		descriptorSetLayout.getHandlePointer());
	    descriptorSet.updateDescriptorImageBuffer(
				opaqueSceneImageView.getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, opaqueSceneSampler.getHandle(),
		    	0, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorImageBuffer(
				opaqueSceneLightScatteringImageView.getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, opaqueSceneLightScatteringSampler.getHandle(),
		    	1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorImageBuffer(
				opaqueSceneDepthMap.getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, opaqueSceneDepthSampler.getHandle(),
		    	2, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorImageBuffer(
				transparencySceneImageView.getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, transparencySceneSampler.getHandle(),
		    	3, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorImageBuffer(
				transparencyLightScatteringImageView.getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, transparencyLightScatteringSampler.getHandle(),
		    	4, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorImageBuffer(
				transparencySceneDepthMap.getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, transparencySceneDepthSampler.getHandle(),
		    	5, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorSet.updateDescriptorImageBuffer(
				transparencyAlphaMap.getHandle(),
		    	VK_IMAGE_LAYOUT_GENERAL, transparencyAlphaSampler.getHandle(),
		    	6, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    
	    List<DescriptorSet> descriptorSets = new ArrayList<DescriptorSet>();
		List<DescriptorSetLayout> descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
	    
		descriptorSets.add(descriptorSet);
		descriptorSetLayouts.add(descriptorSetLayout);
		
		int pushConstantRange = Float.BYTES * 2;
		ByteBuffer pushConstants = memAlloc(pushConstantRange);
		pushConstants.putFloat(width);
		pushConstants.putFloat(height);
		pushConstants.flip();
		
		graphicsPipeline = new GraphicsPipeline(device,
				graphicsShaderPipeline, vertexInput, VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST,
				VkUtil.createLongBuffer(descriptorSetLayouts),
				fbo.getWidth(), fbo.getHeight(),
				fbo.getRenderPass().getHandle(),
				fbo.getColorAttachmentCount(),
				1, pushConstantRange, VK_SHADER_STAGE_FRAGMENT_BIT);
		
		cmdBuffer = new DrawCmdBuffer(
				device, deviceBundle.getLogicalDevice().getGraphicsCommandPool().getHandle(),
				graphicsPipeline.getHandle(), graphicsPipeline.getLayoutHandle(),
				fbo.getRenderPass().getHandle(), fbo.getFrameBuffer().getHandle(),
				fbo.getWidth(), fbo.getHeight(),
				fbo.getColorAttachmentCount(), fbo.getDepthAttachmentCount(),
				VkUtil.createLongArray(descriptorSets),
				vertexBufferObject.getHandle(), indexBufferObject.getHandle(),
				mesh.getIndices().length,
				pushConstants, VK_SHADER_STAGE_FRAGMENT_BIT);
		
		submitInfo = new SubmitInfo();
		submitInfo.setCommandBuffers(cmdBuffer.getHandlePointer());
	}
	
	public void render(){
	
		submitInfo.submit(queue);
	}
	
	public VkImageView getColorAttachment(){
	
		return fbo.getAttachmentImageView(Attachment.COLOR);
	}
	
	public VkImageView getLightScatteringMaskAttachment(){
		
		return fbo.getAttachmentImageView(Attachment.LIGHT_SCATTERING);
	}
	
	private class OpaqueTransparencyBlendFbo extends VkFrameBufferObject{

		public OpaqueTransparencyBlendFbo(VkDevice device,
				VkPhysicalDeviceMemoryProperties memoryProperties) {
			
			width = EngineContext.getConfig().getX_ScreenResolution();
			height = EngineContext.getConfig().getY_ScreenResolution();
			
			VkImageBundle colorAttachment = new FrameBufferColorAttachment(device, memoryProperties,
					width, height, VK_FORMAT_R16G16B16A16_SFLOAT, 1);
			
			VkImageBundle lightScatteringAttachment = new FrameBufferColorAttachment(device, memoryProperties,
					width, height, VK_FORMAT_R16G16B16A16_SFLOAT, 1);
			
			attachments.put(Attachment.COLOR, colorAttachment);
			attachments.put(Attachment.LIGHT_SCATTERING, lightScatteringAttachment);
			
			renderPass = new RenderPass(device);
			renderPass.setAttachment(VK_FORMAT_R16G16B16A16_SFLOAT, 1, VK_IMAGE_LAYOUT_UNDEFINED,
					VK_IMAGE_LAYOUT_GENERAL, VK_ATTACHMENT_LOAD_OP_CLEAR);
			renderPass.setAttachment(VK_FORMAT_R16G16B16A16_SFLOAT, 1, VK_IMAGE_LAYOUT_UNDEFINED,
					VK_IMAGE_LAYOUT_GENERAL, VK_ATTACHMENT_LOAD_OP_CLEAR);
			
			renderPass.addColorAttachmentReference(0, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
			renderPass.addColorAttachmentReference(1, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
			renderPass.setSubpassDependency(VK_SUBPASS_EXTERNAL, 0,
					VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT,
					VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
					VK_ACCESS_MEMORY_READ_BIT,
					VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT,
					VK_DEPENDENCY_BY_REGION_BIT);
			renderPass.setSubpassDependency(0, VK_SUBPASS_EXTERNAL,
					VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
					VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT,
					VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT,
					VK_ACCESS_MEMORY_READ_BIT,
					VK_DEPENDENCY_BY_REGION_BIT);
			renderPass.createSubpass();
			renderPass.createRenderPass();

			depthAttachmentCount = 0;
			colorAttachmentCount = renderPass.getAttachmentCount()-depthAttachmentCount;
			
			LongBuffer pImageViews = memAllocLong(renderPass.getAttachmentCount());
			pImageViews.put(0, attachments.get(Attachment.COLOR).getImageView().getHandle());
			pImageViews.put(1, attachments.get(Attachment.LIGHT_SCATTERING).getImageView().getHandle());

			frameBuffer = new VkFrameBuffer(device, width, height, 1, pImageViews, renderPass.getHandle());
		}

	}

}
