package org.oreon.core.vk.pipeline;

import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO;

import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;

public class PipelineLayout {

	private VkPipelineLayoutCreateInfo pipelineLayout;
	private long handle;
	
	public void createPipelineLayout(){
		
	}
	
	public void specifyPipelineLayout(){
		
		pipelineLayout = VkPipelineLayoutCreateInfo.calloc()
				.sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                .pNext(0)
                .pSetLayouts(null);
	}

	public long getHandle() {
		return handle;
	}

}
