package org.oreon.core.vk.pipeline;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreatePipelineLayout;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.oreon.core.vk.device.LogicalDevice;
import org.oreon.core.vk.util.VKUtil;

public class PipelineLayout {

	private VkPipelineLayoutCreateInfo pipelineLayout;
	private long handle;
	
	public void createPipelineLayout(LogicalDevice device){
		
		LongBuffer pPipelineLayout = memAllocLong(1);
        int err = vkCreatePipelineLayout(device.getHandle(), pipelineLayout, null, pPipelineLayout);
        
        handle = pPipelineLayout.get(0);
        
        memFree(pPipelineLayout);
        pipelineLayout.free();
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create pipeline layout: " + VKUtil.translateVulkanResult(err));
        }
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
