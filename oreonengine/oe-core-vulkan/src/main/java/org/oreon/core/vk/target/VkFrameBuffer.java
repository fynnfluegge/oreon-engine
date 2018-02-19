package org.oreon.core.vk.target;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateFramebuffer;
import static org.lwjgl.vulkan.VK10.vkDestroyFramebuffer;

import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;
import org.oreon.core.vk.util.VKUtil;

public class VkFrameBuffer {
	
	private long handle;
	
	public VkFrameBuffer(VkDevice device, long imageView, VkExtent2D extent, long renderPass) {
		
		LongBuffer pFramebuffer = memAllocLong(1);
        LongBuffer pAttachments = memAllocLong(1);
    	pAttachments.put(0, imageView);
    	
        VkFramebufferCreateInfo framebufferInfo = VkFramebufferCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                .pAttachments(pAttachments)
                .flags(0)
                .height(extent.height())
                .width(extent.width())
                .layers(1)
                .pNext(0)
                .renderPass(renderPass);
        
        int err = vkCreateFramebuffer(device, framebufferInfo, null, pFramebuffer);
        
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create framebuffer: " + VKUtil.translateVulkanResult(err));
        }
        
        handle = pFramebuffer.get(0);
        
        framebufferInfo.free();
        memFree(pFramebuffer);
        memFree(pAttachments);
	}
	
	public void destroy(VkDevice device){
		
		vkDestroyFramebuffer(device, handle, null);
	}

	public long getHandle() {
		return handle;
	}

}
