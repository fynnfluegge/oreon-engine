package org.oreon.vk.engine;

import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkInstance;
import org.oreon.core.context.EngineContext;
import org.oreon.core.system.RenderEngine;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.context.VulkanInstance;
import org.oreon.core.vk.core.descriptor.DescriptorKeys.DescriptorPoolType;
import org.oreon.core.vk.core.descriptor.DescriptorPool;
import org.oreon.core.vk.core.device.LogicalDevice;
import org.oreon.core.vk.core.device.PhysicalDevice;
import org.oreon.core.vk.core.swapchain.SwapChain;
import org.oreon.core.vk.core.util.VkUtil;
import org.oreon.core.vk.wrapper.framebuffer.OffScreenFbo;

public class VkRenderEngine extends RenderEngine{
	
	private VkInstance vkInstance;
	private PhysicalDevice physicalDevice;
	private LogicalDevice logicalDevice;
	private SwapChain swapChain;
	private long surface;
	
	private OffScreenFbo offScreenFbo;
	
	private ByteBuffer[] layers = {
	            	memUTF8("VK_LAYER_LUNARG_standard_validation"),
				};
	private final boolean validationEnabled = Boolean.parseBoolean(System.getProperty("vulkan.validation", "true"));
	private PointerBuffer ppEnabledLayerNames;
	
	@Override
	public void init() {
		
		super.init();
		
		if (!glfwVulkanSupported()) {
	            throw new AssertionError("GLFW failed to find the Vulkan loader");
	        }
		
		PointerBuffer requiredExtensions = glfwGetRequiredInstanceExtensions();
        if (requiredExtensions == null) {
            throw new AssertionError("Failed to find list of required Vulkan extensions");
        }
        
        ppEnabledLayerNames = VkUtil.getValidationLayerNames(validationEnabled, layers);
        
        VulkanInstance vulkanInstance = new VulkanInstance();
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

	    DescriptorPool imageSamplerDescriptorPool = new DescriptorPool();
	    imageSamplerDescriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, 2);
	    imageSamplerDescriptorPool.create(logicalDevice.getHandle(), 2);
	    
	    DescriptorPool uniformBufferDescriptorPool = new DescriptorPool();
	    uniformBufferDescriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 1);
	    uniformBufferDescriptorPool.create(logicalDevice.getHandle(), 1);
	    
	    VkContext.getEnvironment().addDescriptorPool(DescriptorPoolType.COMBINED_IMAGE_SAMPLER,
	    											 imageSamplerDescriptorPool);
	    VkContext.getEnvironment().addDescriptorPool(DescriptorPoolType.UNIFORM_BUFFER,
	    											 uniformBufferDescriptorPool);
	    
	    camera = EngineContext.getCamera();
	    camera.init();
	    
	    offScreenFbo = new OffScreenFbo(logicalDevice.getHandle(),
	    								physicalDevice.getMemoryProperties());
	    
	    VkContext.registerObject(offScreenFbo);
		
		//-----------------------------------------------------------------------------------
	    
		// descriptors
//	    DescriptorSetLayout descriptorLayout = new DescriptorSetLayout(logicalDevice.getHandle(),1);
//	    descriptorLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
//	    								     VK_SHADER_STAGE_FRAGMENT_BIT);
//	    descriptorLayout.create();
//	    
//	    DescriptorSet descriptorSet = new DescriptorSet(logicalDevice.getHandle(), 
//	    					VkContext.getEnvironment().getDescriptorPool(DescriptorPoolType.COMBINED_IMAGE_SAMPLER).getHandle(),
//	    					descriptorLayout.getHandle());
//	    descriptorSet.updateDescriptorImageBuffer(imageView.getHandle(), sampler.getHandle(), 0);
//	    
//	    long[] descriptorSets = new long[2];
//	    descriptorSets[0] = VkContext.getEnvironment().getDescriptorSet(DescriptorSetKey.CAMERA).getSet().getHandle();
//	    descriptorSets[1] = descriptorSet.getHandle();
//	    
//	    LongBuffer descriptorSetLayouts = memAllocLong(2);
//	    descriptorSetLayouts.put(VkContext.getEnvironment().getDescriptorSet(DescriptorSetKey.CAMERA).getLayout().getHandle());
//	    descriptorSetLayouts.put(descriptorLayout.getHandle());
//	    descriptorSetLayouts.flip();
//	    
//	    ShaderPipeline shaderPipeline = new ShaderPipeline(logicalDevice.getHandle());
//	    shaderPipeline.createVertexShader("shaders/vert.spv");
//	    shaderPipeline.createFragmentShader("shaders/frag.spv");
//	    shaderPipeline.createShaderPipeline();
	    
	    swapChain = new SwapChain(logicalDevice,
	    						  physicalDevice,
	    						  surface, 
	    						  offScreenFbo.getImageView().getHandle());
	}
    

	@Override
	public void render() {
		
		// wait for queues to be finished before start draw command
		vkQueueWaitIdle(logicalDevice.getGraphicsQueue());
		
		sceneGraph.render();
		
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
		VkContext.getEnvironment().shutdown();
		logicalDevice.destroy();

		VkContext.getVulkanInstance().destroy();		
	}
	
}
