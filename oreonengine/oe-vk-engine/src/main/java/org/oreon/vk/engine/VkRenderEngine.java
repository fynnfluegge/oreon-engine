package org.oreon.vk.engine;

import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkInstance;
import org.oreon.core.context.EngineContext;
import org.oreon.core.system.RenderEngine;
import org.oreon.core.vk.core.command.SubmitInfo;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.context.VulkanInstance;
import org.oreon.core.vk.core.descriptor.DescriptorPool;
import org.oreon.core.vk.core.device.LogicalDevice;
import org.oreon.core.vk.core.device.PhysicalDevice;
import org.oreon.core.vk.core.swapchain.SwapChain;
import org.oreon.core.vk.core.util.VkUtil;

public class VkRenderEngine extends RenderEngine{
	
	private VkInstance vkInstance;
	private PhysicalDevice physicalDevice;
	private LogicalDevice logicalDevice;
	private SwapChain swapChain;
	private long surface;

	private OffScreenPrimaryCmdBuffer offScreenPrimaryCmdBuffer;
	private OffScreenFbo offScreenFbo;
	private SubmitInfo offScreenSubmitInfo;
	
	private ByteBuffer[] layers = {
	            	memUTF8("VK_LAYER_LUNARG_standard_validation"),
				};
	private final boolean validationEnabled = true;
	private PointerBuffer ppEnabledLayerNames;
	
	@Override
	public void init() {
		
		super.init();
		
		if (!glfwVulkanSupported()) {
	            throw new AssertionError("GLFW failed to find the Vulkan loader");
	        }
        
        ppEnabledLayerNames = VkUtil.getValidationLayerNames(validationEnabled, layers);
        
        VulkanInstance vulkanInstance = new VulkanInstance(ppEnabledLayerNames);
        VkContext.registerObject(vulkanInstance);
        
        vkInstance = vulkanInstance.getHandle();
       
        LongBuffer pSurface = memAllocLong(1);
	    int err = glfwCreateWindowSurface(vkInstance, EngineContext.getWindow().getId(), null, pSurface);
	    
	    surface = pSurface.get(0);
	    if (err != VK_SUCCESS) {
	        throw new AssertionError("Failed to create surface: " + VkUtil.translateVulkanResult(err));
	    }
	    
        physicalDevice = new PhysicalDevice(vkInstance, surface);
        
	    logicalDevice = new LogicalDevice();
	    logicalDevice.createDevice(physicalDevice, 0, ppEnabledLayerNames);
	    
	    VkContext.registerObject(physicalDevice);
	    VkContext.registerObject(logicalDevice);

	    DescriptorPool imageSamplerDescriptorPool = new DescriptorPool(4);
	    imageSamplerDescriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, 6);
	    imageSamplerDescriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_STORAGE_IMAGE, 30);
	    imageSamplerDescriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_STORAGE_BUFFER, 1);
	    imageSamplerDescriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 8);
	    imageSamplerDescriptorPool.create(logicalDevice.getHandle(), 45);
	    
	    VkContext.getDescriptorPoolManager().addDescriptorPool("POOL_1",
	    											 imageSamplerDescriptorPool);
	    
	    camera = EngineContext.getCamera();
	    camera.init();
	    
	    offScreenFbo = new OffScreenFbo(logicalDevice.getHandle(),
	    								physicalDevice.getMemoryProperties());
	    
	    offScreenPrimaryCmdBuffer =  new OffScreenPrimaryCmdBuffer(logicalDevice.getHandle(),
	    		logicalDevice.getGraphicsCommandPool().getHandle());
	    offScreenSubmitInfo = new SubmitInfo();
	    offScreenSubmitInfo.setCommandBuffers(offScreenPrimaryCmdBuffer.getHandlePointer());

	    VkContext.getRenderState().setOffScreenFrameBuffer(offScreenFbo.getFrameBuffer());
	    VkContext.getRenderState().setOffScreenRenderPass(offScreenFbo.getRenderPass());
	    VkContext.getRenderState().setOffScreenAttachmentCount(offScreenFbo.getAttachmentCount());
	    
	    swapChain = new SwapChain(logicalDevice,
	    						  physicalDevice,
	    						  surface,
	    						  offScreenFbo.getGBuffer().getAlbedoBuffer().getImageView().getHandle());
	}
    

	@Override
	public void render() {
		
		sceneGraph.render();

		// record secondary command buffers from scenegraph into
		// primary render command buffer
		offScreenPrimaryCmdBuffer.reset();
		offScreenPrimaryCmdBuffer.record(offScreenFbo.getRenderPass().getHandle(),
				offScreenFbo.getFrameBuffer().getHandle(),
				offScreenFbo.getWidth(),
				offScreenFbo.getHeight(),
				offScreenFbo.getAttachmentCount(),
				offScreenFbo.isDepthAttachment(),
				VkUtil.createPointerBuffer(VkContext.getRenderState().getOffScreenSecondaryCmdBuffers()));
		
		offScreenSubmitInfo.submit(logicalDevice.getGraphicsQueue());
		
		vkQueueWaitIdle(logicalDevice.getGraphicsQueue());
		swapChain.draw(logicalDevice.getGraphicsQueue());
	}

	@Override
	public void update() {

		super.update();
	}

	@Override
	public void shutdown() {
		
		super.shutdown();
		
		// wait for queues to be finished before destroy vulkan objects
		vkQueueWaitIdle(logicalDevice.getGraphicsQueue());
		vkQueueWaitIdle(logicalDevice.getTransferQueue());
		vkDestroySwapchainKHR(logicalDevice.getHandle(), swapChain.getHandle(), null);
		swapChain.destroy();
		EngineContext.getCamera().shutdown();
		VkContext.getDescriptorPoolManager().shutdown();
		logicalDevice.destroy();

		VkContext.getVulkanInstance().destroy();		
	}
	
}
