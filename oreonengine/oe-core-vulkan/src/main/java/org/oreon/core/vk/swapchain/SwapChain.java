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
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.CommandPool;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.core.vk.synchronization.VkSemaphore;
import org.oreon.core.vk.target.VkFrameBuffer;
import org.oreon.core.vk.util.VkUtil;

import lombok.Getter;

public class SwapChain {
	
	@Getter
	private long handle;
	
	private LongBuffer pHandle;
	private VkExtent2D extent;
	private List<Long> swapChainImages;
	private List<VkImageView> swapChainImageViews;
	private List<VkFrameBuffer> frameBuffers;
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
            throw new AssertionError("Failed to create swap chain: " + VkUtil.translateVulkanResult(err));
        }
        
        createImages(device);
        createImageViews(device, imageFormat);
        createFrameBuffers(device, renderPass);
        
        renderCompleteSemaphore = new VkSemaphore(device);
        imageAcquiredSemaphore = new VkSemaphore(device);
        pAcquiredImageIndex = memAllocInt(1);
        
        presentInfo = VkPresentInfoKHR.calloc()
                .sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                .pNext(0)
                .pWaitSemaphores(renderCompleteSemaphore.getPHandle())
                .swapchainCount(1)
                .pSwapchains(pHandle)
                .pImageIndices(pAcquiredImageIndex)
                .pResults(null);
        
        swapchainCreateInfo.free();
	}
	
	public void createImages(VkDevice device){
		
		IntBuffer pImageCount = memAllocInt(1);
        int err = vkGetSwapchainImagesKHR(device, handle, pImageCount, null);
        int imageCount = pImageCount.get(0);
        if (err != VK_SUCCESS) {
        	throw new AssertionError("Failed to get number of swapchain images: " + VkUtil.translateVulkanResult(err));
        }

        LongBuffer pSwapchainImages = memAllocLong(imageCount);
        err = vkGetSwapchainImagesKHR(device, handle, pImageCount, pSwapchainImages);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to get swapchain images: " + VkUtil.translateVulkanResult(err));
        }
        
        swapChainImages = new ArrayList<>(imageCount);
        for (int i = 0; i < imageCount; i++) {
        	swapChainImages.add(pSwapchainImages.get(i));
        }
        
        memFree(pImageCount);
        memFree(pSwapchainImages);
	}
	
	public void createImageViews(VkDevice device, int imageFormat){
        
        swapChainImageViews = new ArrayList<>(swapChainImages.size());
        for (long swapChainImage : swapChainImages){
        	
        	VkImageView imageView = new VkImageView();
        	imageView.createImageView(device, imageFormat, swapChainImage);
        	
			swapChainImageViews.add(imageView);
        }
	}
	
	public void createFrameBuffers(VkDevice device, long renderPass){
		
		frameBuffers = new ArrayList<>(swapChainImages.size());
        for (VkImageView imageView : swapChainImageViews){
        	
        	VkFrameBuffer frameBuffer = new VkFrameBuffer(device, imageView.getHandle(), extent, renderPass);
        	frameBuffers.add(frameBuffer);
        }
	}
	
	public void createRenderCommandBuffers(VkDevice device, CommandPool commandPool, long pipeline,
			 							   long pipelineLayout, long renderPass,
										   long vertexBuffer, long indexBuffer,
										   long[] descriptorSets){
		
		renderCommandBuffers = new ArrayList<>();
		
		for (VkFrameBuffer framebuffer : frameBuffers){
			CommandBuffer commandBuffer = new CommandBuffer(device, commandPool.getHandle());
			commandBuffer.beginRecord(VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT);
			commandBuffer.recordIndexedRenderCmd(pipeline, pipelineLayout, renderPass,
												  vertexBuffer, indexBuffer, descriptorSets,
												  extent, framebuffer.getHandle());
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
                .waitSemaphoreCount(imageAcquiredSemaphore.getPHandle().remaining())
                .pWaitSemaphores(imageAcquiredSemaphore.getPHandle())
                .pWaitDstStageMask(pWaitDstStageMask)
                .pCommandBuffers(null)
                .pSignalSemaphores(renderCompleteSemaphore.getPHandle());
	}
	
	public void draw(VkDevice device, VkQueue queue){
		
		int err = vkAcquireNextImageKHR(device, handle, UINT64_MAX, imageAcquiredSemaphore.getHandle(), VK_NULL_HANDLE, pAcquiredImageIndex);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to acquire next swapchain image: " + VkUtil.translateVulkanResult(err));
        }
        
        CommandBuffer currentRenderCommandBuffer = renderCommandBuffers.get(pAcquiredImageIndex.get(0));
        submitInfo.pCommandBuffers(currentRenderCommandBuffer.getPCommandBuffer());
        currentRenderCommandBuffer.submit(queue, submitInfo);
		
		err = vkQueuePresentKHR(queue, presentInfo);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to present the swapchain image: " + VkUtil.translateVulkanResult(err));
        }
	}
	
	public void destroy(VkDevice device){
		
		for (VkImageView imageView : swapChainImageViews){
			imageView.destroy(device);
		}
		for (VkFrameBuffer framebuffer : frameBuffers){
			framebuffer.destroy(device);
		}
		renderCompleteSemaphore.destroy(device);
		imageAcquiredSemaphore.destroy(device);
	}

}
