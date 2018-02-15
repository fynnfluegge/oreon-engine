package org.oreon.core.vk.swapchain;

import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkAcquireNextImageKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkCreateSwapchainKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkGetSwapchainImagesKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkQueuePresentKHR;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateFramebuffer;
import static org.lwjgl.vulkan.VK10.vkCreateImageView;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_VIEW_TYPE_2D;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO;
import static org.lwjgl.vulkan.VK10.VK_COMPONENT_SWIZZLE_R;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMPONENT_SWIZZLE_G;
import static org.lwjgl.vulkan.VK10.VK_COMPONENT_SWIZZLE_B;
import static org.lwjgl.vulkan.VK10.VK_COMPONENT_SWIZZLE_A;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.CommandPool;
import org.oreon.core.vk.synchronization.VkSemaphore;
import org.oreon.core.vk.util.VKUtil;

public class SwapChain {
	
	private long handle;
	private LongBuffer pHandle;
	private VkExtent2D extent;
	private List<Long> swapChainImages;
	private List<Long> swapChainImageViews;
	private List<Long> frameBuffers;
	private CommandPool commandPool;
	private List<CommandBuffer> renderCommandBuffers;
	private VkPresentInfoKHR presentInfo;
	private IntBuffer pAcquiredImageIndex;
	private VkSemaphore renderCompleteSemaphore;
	private VkSemaphore imageAcquiredSemaphore;
	private VkSubmitInfo submitInfo;
	
	private final long UINT64_MAX = 0xFFFFFFFFFFFFFFFFL;
	
	public SwapChain(VkDevice device,
					 long surface,
					 int minImageCount,
					 int imageFormat,
					 int colorSpace,
					 int presentMode,
					 VkExtent2D swapExtend,
					 long renderPass) {
		
		extent = swapExtend;
		
		VkSwapchainCreateInfoKHR swapchainCreateInfo = VkSwapchainCreateInfoKHR.calloc()
				.sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
		        .pNext(0)
		        .surface(surface)
		        .oldSwapchain(VK_NULL_HANDLE)
		        .compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
		        .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
		        .preTransform(VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR)
		        .minImageCount(minImageCount)
		        .imageFormat(imageFormat)
		        .imageColorSpace(colorSpace)
		        .imageExtent(extent)
		        .presentMode(presentMode)
		        .imageArrayLayers(1)
		        .clipped(true)
		        // presentation queue family and graphics queue family are the same
		        .imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
                .pQueueFamilyIndices(null);
		
		pHandle = memAllocLong(1);
        int err = vkCreateSwapchainKHR(device, swapchainCreateInfo, null, pHandle);
        handle = pHandle.get(0);
        
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create swap chain: " + VKUtil.translateVulkanResult(err));
        }
        
        IntBuffer pImageCount = memAllocInt(1);
        err = vkGetSwapchainImagesKHR(device, handle, pImageCount, null);
        int imageCount = pImageCount.get(0);
        if (err != VK_SUCCESS) {
        	throw new AssertionError("Failed to get number of swapchain images: " + VKUtil.translateVulkanResult(err));
        }

        LongBuffer pSwapchainImages = memAllocLong(imageCount);
        err = vkGetSwapchainImagesKHR(device, handle, pImageCount, pSwapchainImages);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get swapchain images: " + VKUtil.translateVulkanResult(err));
        }
        
        swapChainImages = new ArrayList<>(imageCount);
        for (int i = 0; i < imageCount; i++) {
        	swapChainImages.add(pSwapchainImages.get(i));
        }
        
        VkImageViewCreateInfo imageViewCreateInfo = VkImageViewCreateInfo.calloc()
        		.sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
	     		.pNext(0)
	     		.viewType(VK_IMAGE_VIEW_TYPE_2D)
	     		.format(imageFormat);
        	imageViewCreateInfo
        		.components()
		            .r(VK_COMPONENT_SWIZZLE_R)
		            .g(VK_COMPONENT_SWIZZLE_G)
		            .b(VK_COMPONENT_SWIZZLE_B)
		            .a(VK_COMPONENT_SWIZZLE_A);
        	imageViewCreateInfo
             	.subresourceRange()
             		.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
	                .baseMipLevel(0)
	                .levelCount(1)
	                .baseArrayLayer(0)
	                .layerCount(1);
        
        swapChainImageViews = new ArrayList<>(imageCount);
        LongBuffer pImageView = memAllocLong(1);
        for (long swapChainImage : swapChainImages){
        	
        	imageViewCreateInfo.image(swapChainImage);
        	
			err = vkCreateImageView(device, imageViewCreateInfo, null, pImageView);
			if (err != VK_SUCCESS) {
			   throw new AssertionError("Failed to create image view: " + VKUtil.translateVulkanResult(err));
			}
			swapChainImageViews.add(pImageView.get(0));
        }
        
        frameBuffers = new ArrayList<>(imageCount);
        LongBuffer pFramebuffer = memAllocLong(1);
        LongBuffer attachments = memAllocLong(1);
        for (long imageView : swapChainImageViews){
        	
        	attachments.put(0, imageView);
            VkFramebufferCreateInfo framebufferInfo = VkFramebufferCreateInfo.calloc()
                    .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                    .pAttachments(attachments)
                    .flags(0)
                    .height(extent.height())
                    .width(extent.width())
                    .layers(1)
                    .pNext(0)
                    .renderPass(renderPass);
            
            err = vkCreateFramebuffer(device, framebufferInfo, null, pFramebuffer);
            long framebuffer = pFramebuffer.get(0);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to create framebuffer: " + VKUtil.translateVulkanResult(err));
            }
            
            frameBuffers.add(framebuffer);
            
            framebufferInfo.free();
        }
        
        renderCompleteSemaphore = new VkSemaphore(device);
        imageAcquiredSemaphore = new VkSemaphore(device);
        pAcquiredImageIndex = memAllocInt(1);
        
        presentInfo = VkPresentInfoKHR.calloc()
                .sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                .pNext(0)
                .pWaitSemaphores(renderCompleteSemaphore.getpHandle())
                .swapchainCount(1)
                .pSwapchains(pHandle)
                .pImageIndices(pAcquiredImageIndex)
                .pResults(null);
        
        memFree(pFramebuffer);
        memFree(attachments);
        memFree(pImageCount);
        memFree(pSwapchainImages);
        memFree(pImageView);
        swapchainCreateInfo.free();
        imageViewCreateInfo.free();
	}
	
	public void createCommandPool(VkDevice device, int queueIndex){
		
		  commandPool = new CommandPool(device, queueIndex);
	}
	
	public void createRenderCommandBuffers(VkDevice device, long pipeline, long renderPass){
		
		renderCommandBuffers = new ArrayList<>();
		
		for (long framebuffer : frameBuffers){
			CommandBuffer commandBuffer = new CommandBuffer(device, commandPool.getHandle());
			commandBuffer.beginRecord();
			commandBuffer.recordRenderPass(pipeline, renderPass, extent, framebuffer);
			commandBuffer.finishRecord();
			renderCommandBuffers.add(commandBuffer);
		}
	}
	
	public void createSubmitInfo(){
		
		IntBuffer pWaitDstStageMask = memAllocInt(1);
        pWaitDstStageMask.put(0, VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
		
		submitInfo = VkSubmitInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                .pNext(0)
                .waitSemaphoreCount(imageAcquiredSemaphore.getpHandle().remaining())
                .pWaitSemaphores(imageAcquiredSemaphore.getpHandle())
                .pWaitDstStageMask(pWaitDstStageMask)
                .pCommandBuffers(null)
                .pSignalSemaphores(renderCompleteSemaphore.getpHandle());
	}
	
	public void draw(VkDevice device, VkQueue queue){
		
		int err = vkAcquireNextImageKHR(device, handle, UINT64_MAX, imageAcquiredSemaphore.getHandle(), VK_NULL_HANDLE, pAcquiredImageIndex);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to acquire next swapchain image: " + VKUtil.translateVulkanResult(err));
        }
        
        CommandBuffer currentRenderCommandBuffer = renderCommandBuffers.get(pAcquiredImageIndex.get(0));
        currentRenderCommandBuffer.submit(queue, submitInfo);
		
		err = vkQueuePresentKHR(queue, presentInfo);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to present the swapchain image: " + VKUtil.translateVulkanResult(err));
        }
	}
	
	public void shutdown(){
		
	}

	public long getHandle() {
		return handle;
	}

}
