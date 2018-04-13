package org.oreon.core.vk.core.swapchain;

import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.vulkan.KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_FIFO_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkAcquireNextImageKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkCreateSwapchainKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkGetSwapchainImagesKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkQueuePresentKHR;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;
import org.oreon.core.context.EngineContext;
import org.oreon.core.model.Mesh;
import org.oreon.core.model.Vertex.VertexLayout;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.util.MeshGenerator;
import org.oreon.core.vk.core.buffer.VkBuffer;
import org.oreon.core.vk.core.command.CommandBuffer;
import org.oreon.core.vk.core.command.CommandPool;
import org.oreon.core.vk.core.command.SubmitInfo;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.descriptor.Descriptor;
import org.oreon.core.vk.core.device.LogicalDevice;
import org.oreon.core.vk.core.device.PhysicalDevice;
import org.oreon.core.vk.core.framebuffer.VkFrameBuffer;
import org.oreon.core.vk.core.image.VkImageView;
import org.oreon.core.vk.core.synchronization.VkSemaphore;
import org.oreon.core.vk.core.util.VkUtil;
import org.oreon.core.vk.wrapper.VkMemoryHelper;
import org.oreon.core.vk.wrapper.descriptor.SwapChainDescriptor;
import org.oreon.core.vk.wrapper.pipeline.SwapChainPipeline;
import org.oreon.core.vk.wrapper.renderpass.SwapChainRenderPass;

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
	private SubmitInfo submitInfo;
	
	private SwapChainPipeline pipeline;
	private SwapChainRenderPass renderPass;
	private Descriptor descriptor;
	
	private VkDevice device;
	
	private final long UINT64_MAX = 0xFFFFFFFFFFFFFFFFL;
	
	public SwapChain(LogicalDevice logicalDevice,
					 PhysicalDevice physicalDevice,
					 long surface,
					 long imageView) {
		
		this.device = logicalDevice.getHandle();
		
		extent = VkContext.getPhysicalDevice().getSwapChainCapabilities().getSurfaceCapabilities().currentExtent();
	    extent.width(EngineContext.getWindow().getWidth());
	    extent.height(EngineContext.getWindow().getHeight());
		
		int imageFormat = VK_FORMAT_B8G8R8A8_UNORM;
	    int colorSpace = VK_COLOR_SPACE_SRGB_NONLINEAR_KHR;
	    
	    VkContext.getPhysicalDevice().checkDeviceFormatAndColorSpaceSupport(imageFormat, colorSpace);
	    
		int presentMode = VK_PRESENT_MODE_MAILBOX_KHR;
		
	    if (!VkContext.getPhysicalDevice().checkDevicePresentationModeSupport(presentMode)){
	    	
	    	if (VkContext.getPhysicalDevice().checkDevicePresentationModeSupport(VK_PRESENT_MODE_FIFO_KHR))
	    		presentMode = VK_PRESENT_MODE_FIFO_KHR;
	    	else
	    		presentMode = VK_PRESENT_MODE_IMMEDIATE_KHR;
	    }
	    
	    int minImageCount = VkContext.getPhysicalDevice().getDeviceMinImageCount4TripleBuffering();
	    
	    descriptor = new SwapChainDescriptor(device, imageView);
	    long[] descriptorSets = new long[1];
	    descriptorSets[0] = descriptor.getSet().getHandle();
	    
	    renderPass = new SwapChainRenderPass(device, imageFormat);
	    pipeline = new SwapChainPipeline(device, renderPass.getHandle(), extent, imageFormat, descriptor.getLayout().getHandle());
		
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
        
        createImages();
        createImageViews(imageFormat);
        createFrameBuffers(renderPass.getHandle());
        
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
        
        Mesh fullScreenQuad = MeshGenerator.NDCQuad2D();
        ByteBuffer vertexBuffer = BufferUtil.createByteBuffer(fullScreenQuad.getVertices(), VertexLayout.POS_UV);
        ByteBuffer indexBuffer = BufferUtil.createByteBuffer(fullScreenQuad.getIndices());
        
        VkBuffer vertexBufferObject = VkMemoryHelper.createDeviceLocalBuffer(device,
        													physicalDevice.getMemoryProperties(),
        													logicalDevice.getTransferCommandPool().getHandle(),
        													logicalDevice.getTransferQueue(),
        													vertexBuffer, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);
        
        VkBuffer indexBufferObject = VkMemoryHelper.createDeviceLocalBuffer(device,
        													physicalDevice.getMemoryProperties(),
        													logicalDevice.getTransferCommandPool().getHandle(),
        													logicalDevice.getTransferQueue(),
        													indexBuffer, VK_BUFFER_USAGE_INDEX_BUFFER_BIT);
        
        createRenderCommandBuffers(logicalDevice.getGraphicsCommandPool(),
        		renderPass.getHandle(), 
        		vertexBufferObject.getHandle(),
				indexBufferObject.getHandle(),
				6, descriptorSets);
        
        createSubmitInfo();
	}
	
	public void createImages(){
		
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
	
	public void createImageViews(int imageFormat){
        
        swapChainImageViews = new ArrayList<>(swapChainImages.size());
        for (long swapChainImage : swapChainImages){
        	
        	VkImageView imageView = new VkImageView(device, imageFormat, swapChainImage);
        	
			swapChainImageViews.add(imageView);
        }
	}
	
	public void createFrameBuffers(long renderPass){
		
		frameBuffers = new ArrayList<>(swapChainImages.size());
        for (VkImageView imageView : swapChainImageViews){
        	
        	VkFrameBuffer frameBuffer = new VkFrameBuffer(device,
        			extent.width(), extent.height(), 1, imageView.getHandle(), renderPass);
        	frameBuffers.add(frameBuffer);
        }
	}
	
	public void createRenderCommandBuffers(CommandPool commandPool, long renderPass,
										   long vertexBuffer, long indexBuffer, int indexCount,
										   long[] descriptorSets){
		
		renderCommandBuffers = new ArrayList<>();
		
		for (VkFrameBuffer framebuffer : frameBuffers){
			CommandBuffer commandBuffer = new CommandBuffer(device, commandPool.getHandle());
			commandBuffer.beginRecord(VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT);
			commandBuffer.recordIndexedRenderCmd(pipeline, renderPass,
												 vertexBuffer, indexBuffer, indexCount, descriptorSets,
												 extent.width(), extent.height(), framebuffer.getHandle());
			commandBuffer.finishRecord();
			renderCommandBuffers.add(commandBuffer);
		}
	}
	
	public void createSubmitInfo(){
		
		IntBuffer pWaitDstStageMask = memAllocInt(1);
        pWaitDstStageMask.put(0, VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
		
        submitInfo = new SubmitInfo();
        submitInfo.setWaitDstStageMask(pWaitDstStageMask);
        submitInfo.setWaitSemaphores(imageAcquiredSemaphore.getPHandle());
        submitInfo.setSignalSemaphores(renderCompleteSemaphore.getPHandle());
	}
	
	public void draw(VkQueue queue){
		
		int err = vkAcquireNextImageKHR(device, handle, UINT64_MAX, imageAcquiredSemaphore.getHandle(), VK_NULL_HANDLE, pAcquiredImageIndex);
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to acquire next swapchain image: " + VkUtil.translateVulkanResult(err));
        }
        
        CommandBuffer currentRenderCommandBuffer = renderCommandBuffers.get(pAcquiredImageIndex.get(0));
        submitInfo.setCommandBuffers(currentRenderCommandBuffer.getPHandle());

        submitInfo.submit(queue);
		VkUtil.vkCheckResult(vkQueuePresentKHR(queue, presentInfo));
	}
	
	public void destroy(){
		
		for (VkImageView imageView : swapChainImageViews){
			imageView.destroy();
		}
		for (VkFrameBuffer framebuffer : frameBuffers){
			framebuffer.destroy();
		}
		renderCompleteSemaphore.destroy();
		imageAcquiredSemaphore.destroy();
		descriptor.destroy();
		pipeline.destroy();
		renderPass.destroy();
	}

}
