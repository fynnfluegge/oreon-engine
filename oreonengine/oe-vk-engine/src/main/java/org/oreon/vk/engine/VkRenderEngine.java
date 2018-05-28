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
import java.util.LinkedHashMap;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkInstance;
import org.oreon.core.context.EngineContext;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.RenderList;
import org.oreon.core.system.RenderEngine;
import org.oreon.core.target.Attachment;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.SubmitInfo;
import org.oreon.core.vk.context.VkContext;
import org.oreon.core.vk.context.VulkanInstance;
import org.oreon.core.vk.descriptor.DescriptorPool;
import org.oreon.core.vk.device.LogicalDevice;
import org.oreon.core.vk.device.PhysicalDevice;
import org.oreon.core.vk.scenegraph.VkRenderInfo;
import org.oreon.core.vk.swapchain.SwapChain;
import org.oreon.core.vk.util.VkUtil;
import org.oreon.core.vk.wrapper.buffer.VkUniformBuffer;
import org.oreon.core.vk.wrapper.command.PrimaryCmdBuffer;
import org.oreon.vk.components.filter.SSAO;

public class VkRenderEngine extends RenderEngine{
	
	private VkInstance vkInstance;
	private static PhysicalDevice physicalDevice;
	private static LogicalDevice logicalDevice;
	private static SwapChain swapChain;
	private static long surface;

	private OffScreenFbo offScreenFbo;
	private ReflectionFbo reflectionFbo;
	private PrimaryCmdBuffer offScreenPrimaryCmdBuffer;
	private LinkedHashMap<String, CommandBuffer> offScreenSecondaryCmdBuffers;
	private RenderList offScreenRenderList;
	private SubmitInfo offScreenSubmitInfo;
	
	// uniform buffers
	private VkUniformBuffer renderStateUbo;
	
	// post processing filter
	private SSAO ssao;
	
	private ByteBuffer[] layers = {
	            	memUTF8("VK_LAYER_LUNARG_standard_validation"),
				};
	private final boolean validationEnabled = true;
	private PointerBuffer ppEnabledLayerNames;
	
	@Override
	public void init() {
		
		super.init();
		
		offScreenRenderList = new RenderList();
		offScreenSecondaryCmdBuffers = new LinkedHashMap<String, CommandBuffer>();
		
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
	    imageSamplerDescriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, 10);
	    imageSamplerDescriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_STORAGE_IMAGE, 37);
	    imageSamplerDescriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_STORAGE_BUFFER, 2);
	    imageSamplerDescriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 10);
	    imageSamplerDescriptorPool.create(logicalDevice.getHandle(), 59);
	    
	    VkContext.getDescriptorPoolManager().addDescriptorPool("POOL_1",
	    											 imageSamplerDescriptorPool);
	    
	    camera = EngineContext.getCamera();
	    camera.init();
	    
	    offScreenFbo = new OffScreenFbo(logicalDevice.getHandle(),
	    								physicalDevice.getMemoryProperties());
	    reflectionFbo = new ReflectionFbo(logicalDevice.getHandle(),
				physicalDevice.getMemoryProperties());
	    
	    offScreenPrimaryCmdBuffer =  new PrimaryCmdBuffer(logicalDevice.getHandle(),
	    		logicalDevice.getGraphicsCommandPool().getHandle());
	    offScreenSubmitInfo = new SubmitInfo();
	    offScreenSubmitInfo.setCommandBuffers(offScreenPrimaryCmdBuffer.getHandlePointer());

	    VkContext.getRenderState().setOffScreenFbo(offScreenFbo);
	    VkContext.getRenderState().setOffScreenReflectionFbo(reflectionFbo);
	    
//	    swapChain = new SwapChain(logicalDevice, physicalDevice, surface,
//	    		offScreenFbo.getAttachmentImageView(Attachment.POSITION).getHandle());
	    
	    ssao = new SSAO(logicalDevice.getHandle(),
	    		physicalDevice.getMemoryProperties(),
	    		EngineContext.getConfig().getX_ScreenResolution(),
	    		EngineContext.getConfig().getY_ScreenResolution(),
	    		offScreenFbo.getAttachments().get(Attachment.POSITION).getImage(),
	    		offScreenFbo.getAttachmentImageView(Attachment.POSITION),
	    		offScreenFbo.getAttachmentImageView(Attachment.NORMAL));
	}
    
	// FOR TESTING
	// pass static reference to swapchain for display desired imageView
	public static void createSwapChain(){
		
		swapChain = new SwapChain(logicalDevice, physicalDevice, surface,
	    		SSAO.ssaoImageView.getHandle());
	}

	@Override
	public void render() {
		
		sceneGraph.render();
		sceneGraph.record(offScreenRenderList);
		
		// TODO check if offScreenRenderList changed, 
		// if and only if changed rearrange commandBuffers 
		for (String key : offScreenRenderList.getKeySet()) {
			
			if(!offScreenSecondaryCmdBuffers.containsKey(key)){
				VkRenderInfo mainRenderInfo = offScreenRenderList.get(key)
						.getComponent(NodeComponentType.MAIN_RENDERINFO);
				offScreenSecondaryCmdBuffers.put(key, mainRenderInfo.getCommandBuffer());
			}
		}
		
		// primary render command buffer
		if (!offScreenRenderList.getObjectList().isEmpty()){
			offScreenPrimaryCmdBuffer.reset();
			offScreenPrimaryCmdBuffer.record(offScreenFbo.getRenderPass().getHandle(),
					offScreenFbo.getFrameBuffer().getHandle(),
					offScreenFbo.getWidth(),
					offScreenFbo.getHeight(),
					offScreenFbo.getColorAttachmentCount(),
					offScreenFbo.getDepthAttachment(),
					VkUtil.createPointerBuffer(offScreenSecondaryCmdBuffers.values()));
			
			offScreenSubmitInfo.submit(logicalDevice.getGraphicsQueue());
		}
		
		vkQueueWaitIdle(logicalDevice.getGraphicsQueue());
		ssao.render();
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
