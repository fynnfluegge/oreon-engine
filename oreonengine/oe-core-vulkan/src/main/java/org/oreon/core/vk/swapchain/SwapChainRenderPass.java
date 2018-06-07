package org.oreon.core.vk.swapchain;

import static org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_LOAD_OP_CLEAR;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SUBPASS_EXTERNAL;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.vk.pipeline.RenderPass;

public class SwapChainRenderPass extends RenderPass{
	
	public SwapChainRenderPass(VkDevice device, int imageFormat) {
		
		super(device);
		
		setAttachment(imageFormat, 1,
				VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_PRESENT_SRC_KHR,
				VK_ATTACHMENT_LOAD_OP_CLEAR);
		addColorAttachmentReference(0, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
	    setSubpassDependency(VK_SUBPASS_EXTERNAL, 0, VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
	    		VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT, 0,
	    		VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT, 0);
	    createSubpass();
	    createRenderPass();
	}

}
