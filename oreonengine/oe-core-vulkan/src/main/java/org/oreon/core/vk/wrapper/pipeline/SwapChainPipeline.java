package org.oreon.core.vk.wrapper.pipeline;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent2D;
import org.oreon.core.model.Vertex.VertexLayout;
import org.oreon.core.vk.core.pipeline.ShaderPipeline;
import org.oreon.core.vk.core.pipeline.VkPipeline;
import org.oreon.core.vk.core.pipeline.VkVertexInput;

public class SwapChainPipeline extends VkPipeline{

	public SwapChainPipeline(VkDevice device, long renderPass, VkExtent2D extent, LongBuffer layouts) {
		
		super(device);
	    
	    ShaderPipeline shaderPipeline = new ShaderPipeline(device);
	    shaderPipeline.createVertexShader("shaders/quad/quad.vert.spv");
	    shaderPipeline.createFragmentShader("shaders/quad/quad.frag.spv");
	    shaderPipeline.createShaderPipeline();
	    
	    VkVertexInput vertexInputInfo = new VkVertexInput(VertexLayout.POS_UV);
	    
	    setVertexInput(vertexInputInfo);
	    setInputAssembly();
	    setViewportAndScissor(extent.width(), extent.height());
	    setRasterizer();
	    setMultisampling();
	    addColorBlendAttachment();
	    setColorBlendState();
	    setDepthAndStencilTest();
	    setDynamicState();
	    setLayout(layouts);
	    createPipeline(shaderPipeline, renderPass);
	}

}
