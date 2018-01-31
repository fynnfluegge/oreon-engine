package org.oreon.core.vk.pipeline;

import org.lwjgl.vulkan.VkQueue;

public class Pipeline {
	
	private GraphicsPipeline graphicsPipeline;
	private VkQueue queueHandle;
	
	public Pipeline() {
		// TODO Auto-generated constructor stub
	}

	public GraphicsPipeline getGraphicsPipeline() {
		return graphicsPipeline;
	}

	public VkQueue getQueueHandle() {
		return queueHandle;
	}
	
}
