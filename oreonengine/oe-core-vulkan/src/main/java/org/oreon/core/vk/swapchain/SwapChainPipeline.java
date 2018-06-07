package org.oreon.core.vk.swapchain;

import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent2D;
import org.oreon.core.model.Vertex.VertexLayout;
import org.oreon.core.vk.pipeline.ShaderPipeline;
import org.oreon.core.vk.pipeline.VkPipeline;
import org.oreon.core.vk.pipeline.VkVertexInput;

public class SwapChainPipeline extends VkPipeline{

	public SwapChainPipeline(VkDevice device, long renderPass, VkExtent2D extent, LongBuffer layouts) {
		
		super(device);
	    
	    ShaderPipeline shaderPipeline = new ShaderPipeline(device);
	    shaderPipeline.createVertexShader("shaders/quad/quad.vert.spv");
	    shaderPipeline.createFragmentShader("shaders/quad/quad.frag.spv");
	    shaderPipeline.createShaderPipeline();
	    
	    VkVertexInput vertexInputInfo = new VkVertexInput(VertexLayout.POS_UV);
	    
	    setVertexInput(vertexInputInfo);
	    setInputAssembly(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);
	    setViewportAndScissor(extent.width(), extent.height());
	    setRasterizer();
	    setMultisampling(1);
	    addColorBlendAttachment();
	    setColorBlendState();
	    setDepthAndStencilTest(false);
	    setDynamicState();
	    setLayout(layouts);
	    createGraphicsPipeline(shaderPipeline, renderPass);
	}

}
