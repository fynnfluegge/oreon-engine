package org.oreon.vk.demo;

import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_UNORM;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.model.Mesh;
import org.oreon.core.scenegraph.ComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.MeshGenerator;
import org.oreon.core.vk.core.command.SubmitInfo;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.descriptor.DescriptorKeys.DescriptorSetKey;
import org.oreon.core.vk.core.image.VkImage;
import org.oreon.core.vk.core.image.VkImageView;
import org.oreon.core.vk.core.pipeline.PipelineResources;
import org.oreon.core.vk.core.pipeline.ShaderPipeline;
import org.oreon.core.vk.core.pipeline.VkVertexInput;
import org.oreon.core.vk.core.scenegraph.VkRenderInfo;
import org.oreon.core.vk.wrapper.VkMemoryHelper;
import org.oreon.core.vk.wrapper.command.RenderCommandBuffer;
import org.oreon.core.vk.wrapper.framebuffer.OffScreenFbo;
import org.oreon.core.vk.wrapper.pipeline.OffScreenRenderPipeline;

public class VkTestObject extends Renderable{
	
	private class PipelineResource extends PipelineResources{
		
		public PipelineResource(){
			
			VkDevice device = VkContext.getLogicalDevice().getHandle();
			
			VkImage image = VkMemoryHelper.createImageFromFile(
					device,
					VkContext.getPhysicalDevice().getMemoryProperties(),
					VkContext.getLogicalDevice().getTransferCommandPool().getHandle(),
					VkContext.getLogicalDevice().getTransferQueue(),
					"images/vulkan-logo.jpg");
			
			VkImageView imageView = new VkImageView(device,
					VK_FORMAT_R8G8B8A8_UNORM, image.getHandle());
			
			VkTestObjectDescriptor descriptor = new VkTestObjectDescriptor(device, imageView.getHandle());
			
			descriptors.add(VkContext.getEnvironment().getDescriptor(DescriptorSetKey.CAMERA));
			descriptors.add(descriptor);
			
			shaderPipeline = new ShaderPipeline(device);
		    shaderPipeline.createVertexShader("shaders/vert.spv");
		    shaderPipeline.createFragmentShader("shaders/frag.spv");
		    shaderPipeline.createShaderPipeline();
		    
		    Mesh quad = MeshGenerator.NDCQuad2Drot180();
		    vertexInput = new VkVertexInput(quad.getVertexLayout());
		}
	}

	public VkTestObject() {
		
	    Mesh quad = MeshGenerator.NDCQuad2Drot180();
		
	    PipelineResource resource = new PipelineResource();
	    
	    OffScreenFbo fbo = VkContext.getObject(OffScreenFbo.class);
	    
	    System.out.println(fbo.getHeight());
	    
	    OffScreenRenderPipeline pipeline = new OffScreenRenderPipeline(VkContext.getLogicalDevice().getHandle(),
	    						resource,
	    						fbo);
	    RenderCommandBuffer commandBuffer = new RenderCommandBuffer(VkContext.getLogicalDevice().getHandle(),
	    		VkContext.getLogicalDevice().getGraphicsCommandPool().getHandle(), 
	    		pipeline, fbo, resource, quad);
	    
	    SubmitInfo submitInfo = new SubmitInfo(commandBuffer.getPHandle());
	    
	    VkRenderInfo renderInfo = new VkRenderInfo(pipeline, commandBuffer, submitInfo,
	    		VkContext.getLogicalDevice().getGraphicsQueue());
	    
	    addComponent(ComponentType.MAIN_RENDERINFO, renderInfo);
	}
}
