package org.oreon.vk.demo;

import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_FILTER_LINEAR;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.context.EngineContext;
import org.oreon.core.math.Vec2f;
import org.oreon.core.model.Mesh;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.MeshGenerator;
import org.oreon.core.vk.buffer.VkBuffer;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.SubmitInfo;
import org.oreon.core.vk.context.VkContext;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.framebuffer.VkFrameBuffer;
import org.oreon.core.vk.image.VkImage;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.core.vk.image.VkSampler;
import org.oreon.core.vk.pipeline.RenderPass;
import org.oreon.core.vk.pipeline.ShaderPipeline;
import org.oreon.core.vk.pipeline.VkPipeline;
import org.oreon.core.vk.pipeline.VkVertexInput;
import org.oreon.core.vk.scenegraph.VkRenderInfo;
import org.oreon.core.vk.synchronization.Fence;
import org.oreon.core.vk.synchronization.VkSemaphore;
import org.oreon.core.vk.util.VkUtil;
import org.oreon.core.vk.wrapper.buffer.VkBufferHelper;
import org.oreon.core.vk.wrapper.command.SecondaryDrawIndexedCmdBuffer;
import org.oreon.core.vk.wrapper.image.VkImageHelper;
import org.oreon.core.vk.wrapper.pipeline.GraphicsPipeline;
import org.oreon.vk.components.fft.FFT;

public class VkTestObject extends Renderable{

	private FFT fft;
	private Fence fence;
	
	private VkBuffer vertexBufferObject;
	private VkBuffer indexBufferObject;
	private VkPipeline graphicsPipeline;
	private int indices;
	
	private VkSemaphore waitSemaphore;
	private VkSemaphore signalSemaphore;
	private SubmitInfo submitInfo;

	public VkTestObject() {
		
		signalSemaphore = new VkSemaphore(VkContext.getLogicalDevice().getHandle());
		
		fft = new FFT(VkContext.getLogicalDevice().getHandle(),
				VkContext.getPhysicalDevice().getMemoryProperties(), 256, 1000,
				20, new Vec2f(1,1), 25, 1);
		
	    Mesh mesh = MeshGenerator.NDCQuad2Drot180();
	    
	    indices = mesh.getIndices().length;
		
	    VkFrameBuffer offscreenFrameBuffer = VkContext.getRenderState().getOffScreenFbo().getFrameBuffer();
	    RenderPass offScreenRenderPass = VkContext.getRenderState().getOffScreenFbo().getRenderPass();
	    int attachments = VkContext.getRenderState().getOffScreenFbo().getAttachmentCount();
	    
	    VkDevice device = VkContext.getLogicalDevice().getHandle();
		
		VkImage image = VkImageHelper.createSampledImageFromFile(
				device,
				VkContext.getPhysicalDevice().getMemoryProperties(),
				VkContext.getLogicalDevice().getTransferCommandPool().getHandle(),
				VkContext.getLogicalDevice().getTransferQueue(),
				"images/vulkan-logo.jpg");
		
		VkImageView imageView = new VkImageView(device,
				VK_FORMAT_R8G8B8A8_UNORM, image.getHandle(), VK_IMAGE_ASPECT_COLOR_BIT);
		
		DescriptorSetLayout layout = new DescriptorSetLayout(device,1);
	    layout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    						VK_SHADER_STAGE_FRAGMENT_BIT);
	    layout.create();
	    
	    VkSampler sampler = new VkSampler(device, VK_FILTER_LINEAR, false);
	    
	    DescriptorSet set = new DescriptorSet(device,
	    		VkContext.getDescriptorPoolManager().getDescriptorPool("POOL_1").getHandle(),
	    		layout.getHandlePointer());
	    set.updateDescriptorImageBuffer(fft.getDyImageView().getHandle(),
	    		VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
	    		sampler.getHandle(), 0, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
		
		List<DescriptorSet> descriptorSets = new ArrayList<DescriptorSet>();
		List<DescriptorSetLayout> descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		descriptorSets.add(VkContext.getCamera().getDescriptor().getSet());
		descriptorSets.add(set);
		descriptorSetLayouts.add(VkContext.getCamera().getDescriptor().getLayout());
		descriptorSetLayouts.add(layout);
		
		ShaderPipeline shaderPipeline = new ShaderPipeline(device);
	    shaderPipeline.createVertexShader("shaders/triangle.vert.spv");
	    shaderPipeline.createFragmentShader("shaders/triangle.frag.spv");
	    shaderPipeline.createShaderPipeline();
	    
	    Mesh quad = MeshGenerator.NDCQuad2Drot180();
	    VkVertexInput vertexInput = new VkVertexInput(quad.getVertexLayout());
	    
	    graphicsPipeline = new GraphicsPipeline(device,
				shaderPipeline, vertexInput, VkUtil.createLongBuffer(descriptorSetLayouts),
				EngineContext.getConfig().getX_ScreenResolution(),
				EngineContext.getConfig().getY_ScreenResolution(),
				VkContext.getRenderState().getOffScreenFbo().getRenderPass().getHandle());
	    
	    ByteBuffer vertexBuffer = BufferUtil.createByteBuffer(mesh.getVertices(), mesh.getVertexLayout());
		ByteBuffer indexBuffer = BufferUtil.createByteBuffer(mesh.getIndices());
		
		vertexBufferObject = VkBufferHelper.createDeviceLocalBuffer(
				VkContext.getLogicalDevice().getHandle(),
				VkContext.getPhysicalDevice().getMemoryProperties(),
				VkContext.getLogicalDevice().getTransferCommandPool().getHandle(),
				VkContext.getLogicalDevice().getTransferQueue(),
				vertexBuffer, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);

        indexBufferObject = VkBufferHelper.createDeviceLocalBuffer(
        		VkContext.getLogicalDevice().getHandle(),
        		VkContext.getPhysicalDevice().getMemoryProperties(),
        		VkContext.getLogicalDevice().getTransferCommandPool().getHandle(),
        		VkContext.getLogicalDevice().getTransferQueue(),
        		indexBuffer, VK_BUFFER_USAGE_INDEX_BUFFER_BIT);
	    
	    CommandBuffer commandBuffer = new SecondaryDrawIndexedCmdBuffer(
	    		VkContext.getLogicalDevice().getHandle(),
	    		VkContext.getLogicalDevice().getGraphicsCommandPool().getHandle(), 
	    		graphicsPipeline.getHandle(), graphicsPipeline.getLayoutHandle(),
	    		VkContext.getRenderState().getOffScreenFbo().getFrameBuffer().getHandle(),
	    		VkContext.getRenderState().getOffScreenFbo().getRenderPass().getHandle(),
	    		0,
	    		VkUtil.createLongArray(descriptorSets),
	    		vertexBufferObject.getHandle(),
	    		indexBufferObject.getHandle(),
	    		mesh.getIndices().length);
	    
	    fence = new Fence(VkContext.getLogicalDevice().getHandle());
	    
	    submitInfo = new SubmitInfo(commandBuffer.getHandlePointer());
	    
	    VkRenderInfo renderInfo = new VkRenderInfo(commandBuffer);
	    
	    addComponent(NodeComponentType.MAIN_RENDERINFO, renderInfo);
	}

	public void render(){
		
		fft.render();
	}
}
