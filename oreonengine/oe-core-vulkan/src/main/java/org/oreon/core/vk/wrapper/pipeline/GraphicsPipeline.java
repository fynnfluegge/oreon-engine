package org.oreon.core.vk.wrapper.pipeline;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.core.pipeline.ShaderPipeline;
import org.oreon.core.vk.core.pipeline.VkPipeline;
import org.oreon.core.vk.core.pipeline.VkVertexInput;

public class GraphicsPipeline extends VkPipeline{

	public GraphicsPipeline(VkDevice device, ShaderPipeline shaderPipeline,
			VkVertexInput vertexInput, LongBuffer layout, int width, int height,
			long renderPass, int pushConstantRange, int pushConstantStageFlags) {
		
		super(device);
		
		setVertexInput(vertexInput);
		setPushConstantsRange(pushConstantStageFlags, pushConstantRange);
		setInputAssembly();
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
		setInputAssembly();
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
