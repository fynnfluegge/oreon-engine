package org.oreon.core.vk.wrapper.pipeline;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.core.framebuffer.FrameBufferObject;
import org.oreon.core.vk.core.pipeline.VkPipeline;
import org.oreon.core.vk.core.pipeline.PipelineResources;

public class OffScreenRenderPipeline extends VkPipeline{
	
	public OffScreenRenderPipeline(VkDevice device, PipelineResources resources,
								   FrameBufferObject fbo) {
		
		super(device);
		
		setVertexInput(resources.getVertexInput());
		setInputAssembly();
	    setViewportAndScissor(fbo.getWidth(), fbo.getHeight());
	    setRasterizer();
	    setMultisampling();
	    setColorBlending();
	    setDepthAndStencilTest();
	    setDynamicState();
	    setLayout(resources.getDescriporSetLayouts());
	    createPipeline(resources.getShaderPipeline(), fbo.getRenderPass().getHandle());
	}

}
