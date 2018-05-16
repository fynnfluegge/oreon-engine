package org.oreon.core.vk.wrapper.pipeline;

import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.pipeline.ShaderPipeline;
import org.oreon.core.vk.pipeline.VkPipeline;
import org.oreon.core.vk.pipeline.VkVertexInput;

public class GraphicsPipeline extends VkPipeline{

	public GraphicsPipeline(VkDevice device, ShaderPipeline shaderPipeline,
			VkVertexInput vertexInput, LongBuffer layout, int width, int height,
			long renderPass, int pushConstantRange, int pushConstantStageFlags) {
		
		super(device);
		
		setVertexInput(vertexInput);
		setPushConstantsRange(pushConstantStageFlags, pushConstantRange);
		setInputAssembly(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);
		setViewportAndScissor(width, height);
		setRasterizer();
		setMultisampling();
		addColorBlendAttachment();
		addColorBlendAttachment();
		setColorBlendState();
		setDepthAndStencilTest(true);
		setDynamicState();
		setLayout(layout);
		createGraphicsPipeline(shaderPipeline, renderPass);
	}
	
	public GraphicsPipeline(VkDevice device, ShaderPipeline shaderPipeline,
			VkVertexInput vertexInput, LongBuffer layout, int width, int height,
			long renderPass) {
		
		super(device);
		
		setVertexInput(vertexInput);
		setInputAssembly(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);
		setViewportAndScissor(width, height);
		setRasterizer();
		setMultisampling();
		addColorBlendAttachment();
		addColorBlendAttachment();
		setColorBlendState();
		setDepthAndStencilTest(true);
		setDynamicState();
		setLayout(layout);
		createGraphicsPipeline(shaderPipeline, renderPass);
	}

}
