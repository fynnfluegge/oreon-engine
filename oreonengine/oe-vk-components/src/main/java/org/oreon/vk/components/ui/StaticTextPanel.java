package org.oreon.vk.components.ui;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_SHADER_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_FILTER_LINEAR;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_FAMILY_IGNORED;
import static org.lwjgl.vulkan.VK10.VK_SAMPLER_ADDRESS_MODE_REPEAT;
import static org.lwjgl.vulkan.VK10.VK_SAMPLER_MIPMAP_MODE_NEAREST;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkQueue;
import org.oreon.common.ui.UITextPanel;
import org.oreon.core.model.Vertex.VertexLayout;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.SubmitInfo;
import org.oreon.core.vk.context.DeviceManager.DeviceType;
import org.oreon.core.vk.context.VkContext;
import org.oreon.core.vk.descriptor.DescriptorPool;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.device.LogicalDevice;
import org.oreon.core.vk.device.VkDeviceBundle;
import org.oreon.core.vk.framebuffer.VkFrameBufferObject;
import org.oreon.core.vk.image.VkImage;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.core.vk.image.VkSampler;
import org.oreon.core.vk.memory.VkBuffer;
import org.oreon.core.vk.pipeline.ShaderPipeline;
import org.oreon.core.vk.pipeline.VkPipeline;
import org.oreon.core.vk.pipeline.VkVertexInput;
import org.oreon.core.vk.util.VkUtil;
import org.oreon.core.vk.wrapper.buffer.VkBufferHelper;
import org.oreon.core.vk.wrapper.command.DrawCmdBuffer;
import org.oreon.core.vk.wrapper.image.VkImageBundle;
import org.oreon.core.vk.wrapper.image.VkImageHelper;
import org.oreon.core.vk.wrapper.pipeline.GraphicsPipeline;

public class StaticTextPanel extends UITextPanel{

	private VkQueue graphicsQueue;
	private VkPipeline graphicsPipeline;
	private CommandBuffer cmdBuffer;
	private SubmitInfo submitInfo;
	private VkImageBundle fontsImageBundle;
	
	public StaticTextPanel(String text, int xPos, int yPos, int xScaling, int yScaling,
			VkFrameBufferObject fbo) {
		super(text, xPos, yPos, xScaling, yScaling);
		
		VkDeviceBundle deviceBundle = VkContext.getDeviceManager().getDeviceBundle(DeviceType.MAJOR_GRAPHICS_DEVICE);
		LogicalDevice device = deviceBundle.getLogicalDevice();
		DescriptorPool descriptorPool = device.getDescriptorPool(Thread.currentThread().getId());
		VkPhysicalDeviceMemoryProperties memoryProperties = 
				VkContext.getDeviceManager().getPhysicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE).getMemoryProperties();
		graphicsQueue = device.getGraphicsQueue();
		
		VkImage fontsImage = VkImageHelper.loadImageFromFile(
				device.getHandle(), memoryProperties,
				device.getTransferCommandPool().getHandle(),
				device.getTransferQueue(),
				"gui/tex/Fonts.png",
				VK_IMAGE_USAGE_SAMPLED_BIT,
				VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
				VK_ACCESS_SHADER_READ_BIT,
				VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT,
				VK_QUEUE_FAMILY_IGNORED);
		
		VkImageView fontsImageView = new VkImageView(device.getHandle(),
				VK_FORMAT_R8G8B8A8_UNORM, fontsImage.getHandle(), 
				VK_IMAGE_ASPECT_COLOR_BIT, 0);
		
		VkSampler sampler = new VkSampler(device.getHandle(), VK_FILTER_LINEAR, false, 1,
				VK_SAMPLER_MIPMAP_MODE_NEAREST, 0, VK_SAMPLER_ADDRESS_MODE_REPEAT);
		
		fontsImageBundle = new VkImageBundle(fontsImage, fontsImageView, sampler);
		
		ShaderPipeline shaderPipeline = new ShaderPipeline(device.getHandle());
	    shaderPipeline.createVertexShader("shaders/ui/ui.vert.spv");
	    shaderPipeline.createFragmentShader("shaders/ui/ui.frag.spv");
	    shaderPipeline.createShaderPipeline();

	    int pushConstantRange = Float.BYTES * 16 + uv.length * 2 * Float.BYTES;
		ByteBuffer pushConstants = memAlloc(pushConstantRange);
		pushConstants.put(BufferUtil.createByteBuffer(getOrthographicMatrix()));
		for (int i=0; i<uv.length; i++){
			pushConstants.putFloat(uv[i].getX());
			pushConstants.putFloat(uv[i].getY());
		}
		pushConstants.flip();
		
		DescriptorSetLayout descriptorSetLayout = new DescriptorSetLayout(device.getHandle(), 8);
	    descriptorSetLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    		VK_SHADER_STAGE_FRAGMENT_BIT);
	    descriptorSetLayout.create();
	    
	    DescriptorSet descriptorSet = new DescriptorSet(device.getHandle(),
	    		descriptorPool.getHandle(),
	    		descriptorSetLayout.getHandlePointer());
	    descriptorSet.updateDescriptorImageBuffer(fontsImageBundle.getImageView().getHandle(),
	    		VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL, fontsImageBundle.getSampler().getHandle(),
	    		0, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    
	    List<DescriptorSet> descriptorSets = new ArrayList<DescriptorSet>();
		List<DescriptorSetLayout> descriptorSetLayouts = new ArrayList<DescriptorSetLayout>();
		
		descriptorSets.add(descriptorSet);
		descriptorSetLayouts.add(descriptorSetLayout);
		
		VkVertexInput vertexInput = new VkVertexInput(VertexLayout.POS2D);
		ByteBuffer vertexBuffer = BufferUtil.createByteBuffer(panel.getVertices(), VertexLayout.POS2D);
		ByteBuffer indexBuffer = BufferUtil.createByteBuffer(panel.getIndices());
		
		VkBuffer vertexBufferObject = VkBufferHelper.createDeviceLocalBuffer(
				device.getHandle(), memoryProperties,
				device.getTransferCommandPool().getHandle(),
				device.getTransferQueue(),
				vertexBuffer, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);

        VkBuffer indexBufferObject = VkBufferHelper.createDeviceLocalBuffer(
        		device.getHandle(), memoryProperties,
        		device.getTransferCommandPool().getHandle(),
        		device.getTransferQueue(),
        		indexBuffer, VK_BUFFER_USAGE_INDEX_BUFFER_BIT);
        
        graphicsPipeline = new GraphicsPipeline(device.getHandle(),
				shaderPipeline, vertexInput, VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST,
				VkUtil.createLongBuffer(descriptorSetLayouts),
				fbo.getWidth(), fbo.getHeight(),
				fbo.getRenderPass().getHandle(),
				fbo.getColorAttachmentCount(),
				1, pushConstantRange, VK_SHADER_STAGE_FRAGMENT_BIT);
		
		cmdBuffer = new DrawCmdBuffer(
				device.getHandle(),
				deviceBundle.getLogicalDevice().getGraphicsCommandPool().getHandle(),
				graphicsPipeline.getHandle(), graphicsPipeline.getLayoutHandle(),
				fbo.getRenderPass().getHandle(), fbo.getFrameBuffer().getHandle(),
				fbo.getWidth(), fbo.getHeight(),
				fbo.getColorAttachmentCount(), fbo.getDepthAttachment(),
				VkUtil.createLongArray(descriptorSets),
				vertexBufferObject.getHandle(), indexBufferObject.getHandle(),
				panel.getIndices().length,
				pushConstants, VK_SHADER_STAGE_FRAGMENT_BIT);
		
		submitInfo = new SubmitInfo();
		submitInfo.setCommandBuffers(cmdBuffer.getHandlePointer());
	}
	
	@Override
	public void render() {
		submitInfo.submit(graphicsQueue);
	}
}
