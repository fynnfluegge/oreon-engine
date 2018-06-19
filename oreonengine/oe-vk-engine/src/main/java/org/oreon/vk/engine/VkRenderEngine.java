package org.oreon.vk.engine;

import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_STORAGE_IMAGE;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkDeviceWaitIdle;
import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;

import java.nio.LongBuffer;
import java.util.LinkedHashMap;

import org.lwjgl.vulkan.VkInstance;
import org.oreon.core.context.EngineContext;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.RenderList;
import org.oreon.core.system.RenderEngine;
import org.oreon.core.target.FrameBufferObject.Attachment;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.SubmitInfo;
import org.oreon.core.vk.context.DeviceManager.DeviceType;
import org.oreon.core.vk.context.VkContext;
import org.oreon.core.vk.descriptor.DescriptorPool;
import org.oreon.core.vk.device.LogicalDevice;
import org.oreon.core.vk.device.PhysicalDevice;
import org.oreon.core.vk.device.VkDeviceBundle;
import org.oreon.core.vk.framebuffer.VkFrameBufferObject;
import org.oreon.core.vk.image.VkImageView;
import org.oreon.core.vk.scenegraph.VkRenderInfo;
import org.oreon.core.vk.swapchain.SwapChain;
import org.oreon.core.vk.util.VkUtil;
import org.oreon.core.vk.wrapper.buffer.VkUniformBuffer;
import org.oreon.core.vk.wrapper.command.PrimaryCmdBuffer;
import org.oreon.vk.components.filter.Bloom;
import org.oreon.vk.components.ui.VkGUI;

import lombok.Setter;

public class VkRenderEngine extends RenderEngine{
	
	private VkInstance vkInstance;
	private SwapChain swapChain;
	private long surface;
	private VkDeviceBundle majorDevice;

	private VkFrameBufferObject offScreenFbo;
	private VkFrameBufferObject reflectionFbo;
	private VkFrameBufferObject transparencyFbo;
	
	private PrimaryCmdBuffer offScreenPrimaryCmdBuffer;
	private LinkedHashMap<String, CommandBuffer> offScreenSecondaryCmdBuffers;
	private RenderList offScreenRenderList;
	private SubmitInfo offScreenSubmitInfo;
	
	private PrimaryCmdBuffer transparencyPrimaryCmdBuffer;
	private LinkedHashMap<String, CommandBuffer> transparencySecondaryCmdBuffers;
	private RenderList transparencyRenderList;
	private SubmitInfo transparencySubmitInfo;
	
	// uniform buffers
	private VkUniformBuffer renderStateUbo;

	private SampleCoverage sampleCoverageMask;
	private DeferredLighting deferredLighting;
	private FXAA fxaa;
	private OpaqueTransparencyBlending opaqueTransparencyBlending;
	
	// post processing filter
	private Bloom bloom;
	
	// gui
	@Setter
	private VkGUI gui;
	
	@Override
	public void init() {
		
		super.init();
		
		offScreenRenderList = new RenderList();
		transparencyRenderList = new RenderList();
		offScreenSecondaryCmdBuffers = new LinkedHashMap<String, CommandBuffer>();
		transparencySecondaryCmdBuffers = new LinkedHashMap<String, CommandBuffer>();
		
        vkInstance = VkContext.getVulkanInstance().getHandle();
       
        LongBuffer pSurface = memAllocLong(1);
	    int err = glfwCreateWindowSurface(vkInstance, EngineContext.getWindow().getId(), null, pSurface);
	    
	    surface = pSurface.get(0);
	    if (err != VK_SUCCESS) {
	        throw new AssertionError("Failed to create surface: " + VkUtil.translateVulkanResult(err));
	    }
	    
        PhysicalDevice physicalDevice = new PhysicalDevice(vkInstance, surface);
	    LogicalDevice logicalDevice = new LogicalDevice(physicalDevice, 0);
	    majorDevice = new VkDeviceBundle(physicalDevice, logicalDevice);
	    VkContext.getDeviceManager().addDevice(DeviceType.MAJOR_GRAPHICS_DEVICE, majorDevice);
	    
	    DescriptorPool descriptorPool = new DescriptorPool(logicalDevice.getHandle(), 4);
	    descriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, 33);
	    descriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_STORAGE_IMAGE, 59);
	    descriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_STORAGE_BUFFER, 2);
	    descriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 12);
	    descriptorPool.create();
	    logicalDevice.addDescriptorPool(Thread.currentThread().getId(), descriptorPool);
	    
	    camera.init();
	    
	    offScreenFbo = new OffScreenFbo(logicalDevice.getHandle(),
	    		physicalDevice.getMemoryProperties());
	    reflectionFbo = new ReflectionFbo(logicalDevice.getHandle(),
				physicalDevice.getMemoryProperties());
	    transparencyFbo = new TransparencyFbo(logicalDevice.getHandle(),
				physicalDevice.getMemoryProperties());
	    
	    VkContext.getResources().setOffScreenFbo(offScreenFbo);
	    VkContext.getResources().setOffScreenReflectionFbo(reflectionFbo);
	    VkContext.getResources().setTransparencyFbo(transparencyFbo);
	    
	    offScreenPrimaryCmdBuffer =  new PrimaryCmdBuffer(logicalDevice.getHandle(),
	    		logicalDevice.getGraphicsCommandPool().getHandle());
	    offScreenSubmitInfo = new SubmitInfo();
	    offScreenSubmitInfo.setCommandBuffers(offScreenPrimaryCmdBuffer.getHandlePointer());
	    
	    transparencyPrimaryCmdBuffer =  new PrimaryCmdBuffer(logicalDevice.getHandle(),
	    		logicalDevice.getGraphicsCommandPool().getHandle());
	    transparencySubmitInfo = new SubmitInfo();
	    transparencySubmitInfo.setCommandBuffers(transparencyPrimaryCmdBuffer.getHandlePointer());
	    
	    sampleCoverageMask = new SampleCoverage(majorDevice,
	    		EngineContext.getConfig().getX_ScreenResolution(),
	    		EngineContext.getConfig().getY_ScreenResolution(),
	    		offScreenFbo.getAttachmentImageView(Attachment.POSITION),
	    		offScreenFbo.getAttachmentImageView(Attachment.LIGHT_SCATTERING));
	    
	    deferredLighting = new DeferredLighting(majorDevice,
	    		EngineContext.getConfig().getX_ScreenResolution(),
	    		EngineContext.getConfig().getY_ScreenResolution(),
	    		offScreenFbo.getAttachmentImageView(Attachment.ALBEDO),
	    		offScreenFbo.getAttachmentImageView(Attachment.POSITION),
	    		offScreenFbo.getAttachmentImageView(Attachment.NORMAL),
	    		offScreenFbo.getAttachmentImageView(Attachment.SPECULAR_EMISSION),
	    		sampleCoverageMask.getSampleCoverageImageView());
	    
	    opaqueTransparencyBlending = new OpaqueTransparencyBlending(majorDevice,
	    		EngineContext.getConfig().getX_ScreenResolution(),
	    		EngineContext.getConfig().getY_ScreenResolution(),
	    		deferredLighting.getDeferredLightingSceneImageView(),
	    		offScreenFbo.getAttachmentImageView(Attachment.DEPTH),
	    		sampleCoverageMask.getLightScatteringImageView(),
	    		transparencyFbo.getAttachmentImageView(Attachment.ALBEDO),
	    		transparencyFbo.getAttachmentImageView(Attachment.DEPTH),
	    		transparencyFbo.getAttachmentImageView(Attachment.ALPHA),
	    		transparencyFbo.getAttachmentImageView(Attachment.LIGHT_SCATTERING));
	    
	    VkImageView displayImageView = opaqueTransparencyBlending.getColorAttachment();

	    if (EngineContext.getConfig().isFxaaEnabled()){
		    fxaa = new FXAA(majorDevice,
		    		EngineContext.getConfig().getX_ScreenResolution(),
		    		EngineContext.getConfig().getY_ScreenResolution(),
		    		displayImageView);
		    
		    displayImageView = fxaa.getFxaaImageView();
	    }
	    
	    if (EngineContext.getConfig().isBloomEnabled()){
		    bloom = new Bloom(majorDevice,
		    		EngineContext.getConfig().getX_ScreenResolution(),
		    		EngineContext.getConfig().getY_ScreenResolution(),
		    		displayImageView);
		    
		    displayImageView = bloom.getBloomSceneImageView();
	    }
	    
	    if (gui != null){
			gui.init(displayImageView);
			displayImageView = gui.getImageView();
		}
	    
	    swapChain = new SwapChain(logicalDevice, physicalDevice, surface, displayImageView.getHandle());
	}
    
	@Override
	public void render() {
		
		sceneGraph.render();
		offScreenRenderList.setChanged(false);
		sceneGraph.record(offScreenRenderList);

		// record new primary Command Buffer if renderList has changed
		if (offScreenRenderList.hasChanged()){		
			offScreenSecondaryCmdBuffers.clear();
			for (String key : offScreenRenderList.getKeySet()) {
				if(!offScreenSecondaryCmdBuffers.containsKey(key)){
					VkRenderInfo mainRenderInfo = offScreenRenderList.get(key)
							.getComponent(NodeComponentType.MAIN_RENDERINFO);
					offScreenSecondaryCmdBuffers.put(key, mainRenderInfo.getCommandBuffer());
				}
			}
		
			// primary render command buffer
			offScreenPrimaryCmdBuffer.reset();
			offScreenPrimaryCmdBuffer.record(offScreenFbo.getRenderPass().getHandle(),
					offScreenFbo.getFrameBuffer().getHandle(),
					offScreenFbo.getWidth(),
					offScreenFbo.getHeight(),
					offScreenFbo.getColorAttachmentCount(),
					offScreenFbo.getDepthAttachmentCount(),
					VkUtil.createPointerBuffer(offScreenSecondaryCmdBuffers.values()));
		}
		
		offScreenSubmitInfo.submit(majorDevice.getLogicalDevice().getGraphicsQueue());
		vkQueueWaitIdle(majorDevice.getLogicalDevice().getGraphicsQueue());
		
		sampleCoverageMask.render();
		vkQueueWaitIdle(majorDevice.getLogicalDevice().getGraphicsQueue());
		deferredLighting.render();
		vkQueueWaitIdle(majorDevice.getLogicalDevice().getComputeQueue());
		
		transparencyRenderList.setChanged(false);
		sceneGraph.recordTransparentObjects(transparencyRenderList);
		
		if (transparencyRenderList.hasChanged()){
			transparencySecondaryCmdBuffers.clear();
			for (String key : transparencyRenderList.getKeySet()) {
				if(!transparencySecondaryCmdBuffers.containsKey(key)){
					VkRenderInfo mainRenderInfo = transparencyRenderList.get(key)
							.getComponent(NodeComponentType.MAIN_RENDERINFO);
					transparencySecondaryCmdBuffers.put(key, mainRenderInfo.getCommandBuffer());
				}
			}
		
			// primary render command buffer
			transparencyPrimaryCmdBuffer.reset();
			transparencyPrimaryCmdBuffer.record(transparencyFbo.getRenderPass().getHandle(),
					transparencyFbo.getFrameBuffer().getHandle(),
					transparencyFbo.getWidth(),
					transparencyFbo.getHeight(),
					transparencyFbo.getColorAttachmentCount(),
					transparencyFbo.getDepthAttachmentCount(),
					VkUtil.createPointerBuffer(transparencySecondaryCmdBuffers.values()));
		}
		
		transparencySubmitInfo.submit(majorDevice.getLogicalDevice().getGraphicsQueue());
		vkQueueWaitIdle(majorDevice.getLogicalDevice().getGraphicsQueue());
		
		opaqueTransparencyBlending.render();
		vkQueueWaitIdle(majorDevice.getLogicalDevice().getGraphicsQueue());
		
		if (EngineContext.getConfig().isFxaaEnabled()){
			fxaa.render();
			vkQueueWaitIdle(majorDevice.getLogicalDevice().getComputeQueue());
		}
		
		if (EngineContext.getConfig().isBloomEnabled()){
			bloom.render();
			vkQueueWaitIdle(majorDevice.getLogicalDevice().getComputeQueue());
		}
		
		if (gui != null){
			gui.render();
		}
		
		vkQueueWaitIdle(majorDevice.getLogicalDevice().getGraphicsQueue());
		swapChain.draw(majorDevice.getLogicalDevice().getGraphicsQueue());
	}

	@Override
	public void update() {

		super.update();
		
		gui.update();
	}

	@Override
	public void shutdown() {
		
		super.shutdown();
		
		// wait for queues to be finished before destroy vulkan objects
		vkDeviceWaitIdle(majorDevice.getLogicalDevice().getHandle());
		vkDestroySwapchainKHR(majorDevice.getLogicalDevice().getHandle(), swapChain.getHandle(), null);
		swapChain.destroy();
		EngineContext.getCamera().shutdown();
		majorDevice.getLogicalDevice().destroy();
		VkContext.getVulkanInstance().destroy();		
	}
	
}
