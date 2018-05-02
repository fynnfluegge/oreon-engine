package org.oreon.vk.engine;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.core.pipeline.ShaderPipeline;
import org.oreon.core.vk.core.pipeline.VkPipeline;
import org.oreon.core.vk.core.pipeline.VkVertexInput;

public class OffScreenRenderPipeline extends VkPipeline{
	
	public OffScreenRenderPipeline(VkDevice device, long renderPass, int width, int height,
			LongBuffer layout, ShaderPipeline shaderPipeline, VkVertexInput vertexInput) {
		
		super(device);
		
		setVertexInput(vertexInput);
		setInputAssembly();
	    setViewportAndScissor(width, height);
	    setRasterizer();
	    setMultisampling();
	    addColorBlendAttachment();
	    addColorBlendAttachment();
	    setColorBlendState();
	    setDepthAndStencilTest();
	    setDynamicState();
	    setLayout(layout);
	    createGraphicsPipeline(shaderPipeline, renderPass);
	}

}
