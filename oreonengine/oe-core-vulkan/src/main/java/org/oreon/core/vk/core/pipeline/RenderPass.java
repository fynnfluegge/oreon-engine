package org.oreon.core.vk.core.pipeline;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_LOAD_OP_CLEAR;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_LOAD_OP_DONT_CARE;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_STORE_OP_DONT_CARE;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_STORE_OP_STORE;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_BIND_POINT_GRAPHICS;
import static org.lwjgl.vulkan.VK10.VK_SAMPLE_COUNT_1_BIT;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateRenderPass;
import static org.lwjgl.vulkan.VK10.vkDestroyRenderPass;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkAttachmentDescription;
import org.lwjgl.vulkan.VkAttachmentReference;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkRenderPassCreateInfo;
import org.lwjgl.vulkan.VkSubpassDependency;
import org.lwjgl.vulkan.VkSubpassDescription;
import org.oreon.core.vk.core.util.VkUtil;

import lombok.Getter;

public class RenderPass {
	
	private VkAttachmentDescription.Buffer attachments;
	private VkAttachmentReference.Buffer attachmentReferences;
	private VkSubpassDescription.Buffer subpass;
	private VkSubpassDependency.Buffer dependencies;
	
	@Getter
	private long handle;
	
	private VkDevice device;
	
	public RenderPass(VkDevice device) {

		this.device = device;
	}
	
	public void createRenderPass(){
		
		VkRenderPassCreateInfo renderPassInfo = VkRenderPassCreateInfo.calloc()
	            .sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
	            .pNext(0)
	            .pAttachments(attachments)
	            .pSubpasses(subpass)
	            .pDependencies(dependencies);
		
		LongBuffer pRenderPass = memAllocLong(1);
        int err = vkCreateRenderPass(device, renderPassInfo, null, pRenderPass);

        handle = pRenderPass.get(0);
        
        memFree(pRenderPass);
        renderPassInfo.free();
        attachmentReferences.free();
        subpass.free();
        dependencies.free();
        attachments.free();
        
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create clear render pass: " + VkUtil.translateVulkanResult(err));
        }
	}
	
	public void setAttachmentDescription(int format, int initialLayout, int finalLayout){
		
		attachments = VkAttachmentDescription.calloc(1)
				.format(format)
				.samples(VK_SAMPLE_COUNT_1_BIT)
				.loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
				.storeOp(VK_ATTACHMENT_STORE_OP_STORE)
				.stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
				.stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
				.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
				.initialLayout(initialLayout)
				.finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR)
				.finalLayout(finalLayout);
	}
	
	public void setAttachmentReferences(){
		
		attachmentReferences = VkAttachmentReference.calloc(1)
                .attachment(0)
                .layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
	}
	
	public void setSubpass(){
		
		subpass = VkSubpassDescription.calloc(1)
                .pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
                .flags(0)
                .pInputAttachments(null)
                .colorAttachmentCount(attachmentReferences.remaining())
                .pColorAttachments(attachmentReferences)
                .pResolveAttachments(null)
                .pDepthStencilAttachment(null)
                .pPreserveAttachments(null);
	}
	
	public void setSubpassDependency(int srcSubpass, int dstSubpass,
									 int srcStageMask, int dstStageMask,
									 int srcAccessMask, int dstAccessMask,
									 int dependencyFlags){
		
		dependencies = VkSubpassDependency.calloc(1)
				.srcSubpass(srcSubpass)
				.dstSubpass(dstSubpass)
				.srcStageMask(srcStageMask)
				.dstStageMask(dstStageMask)
				.srcAccessMask(srcStageMask)
				.dstAccessMask(dstStageMask)
				.dependencyFlags(dependencyFlags);
	}
	
	public void destroy(){
		
		vkDestroyRenderPass(device, handle, null);
	}

}
