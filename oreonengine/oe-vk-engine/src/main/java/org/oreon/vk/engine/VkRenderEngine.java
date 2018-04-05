package org.oreon.vk.engine;

import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.vulkan.KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_FIFO_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32B32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkInstance;
import org.oreon.core.context.EngineContext;
import org.oreon.core.system.RenderEngine;
import org.oreon.core.vk.core.buffers.VkBuffer;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.context.VulkanInstance;
import org.oreon.core.vk.core.descriptor.DescriptorKeys.DescriptorPoolType;
import org.oreon.core.vk.core.descriptor.DescriptorKeys.DescriptorSetKey;
import org.oreon.core.vk.core.descriptor.DescriptorPool;
import org.oreon.core.vk.core.descriptor.DescriptorSet;
import org.oreon.core.vk.core.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.core.device.LogicalDevice;
import org.oreon.core.vk.core.device.PhysicalDevice;
import org.oreon.core.vk.core.image.VkImage;
import org.oreon.core.vk.core.image.VkImageView;
import org.oreon.core.vk.core.image.VkSampler;
import org.oreon.core.vk.core.pipeline.Pipeline;
import org.oreon.core.vk.core.pipeline.RenderPass;
import org.oreon.core.vk.core.pipeline.ShaderPipeline;
import org.oreon.core.vk.core.pipeline.VertexInputInfo;
import org.oreon.core.vk.core.swapchain.SwapChain;
import org.oreon.core.vk.core.util.VkUtil;
import org.oreon.core.vk.wrapper.VkMemoryHelper;

public class VkRenderEngine extends RenderEngine{
	
	private VkInstance vkInstance;
	private PhysicalDevice physicalDevice;
	private LogicalDevice logicalDevice;
	private SwapChain swapChain;
	private long surface;
	
	private Pipeline pipeline;
	
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
	    imageSamplerDescriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, 1);
	    imageSamplerDescriptorPool.create(logicalDevice.getHandle(), 1);
	    
	    DescriptorPool uniformBufferDescriptorPool = new DescriptorPool();
	    uniformBufferDescriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, 1);
	    uniformBufferDescriptorPool.create(logicalDevice.getHandle(), 1);
	    
	    VkContext.getEnvironment().addDescriptorPool(DescriptorPoolType.COMBINED_IMAGE_SAMPLER,
	    											 imageSamplerDescriptorPool);
	    VkContext.getEnvironment().addDescriptorPool(DescriptorPoolType.UNIFORM_BUFFER,
	    											 uniformBufferDescriptorPool);
	    
	    camera = EngineContext.getCamera();
	    camera.init();
		
		//-----------------------------------------------------------------------------------
	    
	    VkExtent2D swapExtent = physicalDevice.getSwapChainCapabilities().getSurfaceCapabilities().currentExtent();
	    swapExtent.width(EngineContext.getWindow().getWidth());
	    swapExtent.height(EngineContext.getWindow().getHeight());
	    
	    int imageFormat = VK_FORMAT_B8G8R8A8_UNORM;
	    int colorSpace = VK_COLOR_SPACE_SRGB_NONLINEAR_KHR;
	    
	    physicalDevice.checkDeviceFormatAndColorSpaceSupport(imageFormat, colorSpace);
	    
	    int presentMode = VK_PRESENT_MODE_MAILBOX_KHR;
	    
	    if (!physicalDevice.checkDevicePresentationModeSupport(presentMode)){
	    	
	    	if (physicalDevice.checkDevicePresentationModeSupport(VK_PRESENT_MODE_FIFO_KHR))
	    		presentMode = VK_PRESENT_MODE_FIFO_KHR;
	    	else
	    		presentMode = VK_PRESENT_MODE_IMMEDIATE_KHR;
	    }
	    
	    int minImageCount = physicalDevice.getDeviceMinImageCount4TripleBuffering();
	    
	    ByteBuffer vertexBuffer = memAlloc(4 * 2 * 4 + 4 * 3 * 4 + 4 * 2 * 4);
        FloatBuffer fb = vertexBuffer.asFloatBuffer();
        fb.put(-1f).put(-1f);
        fb.put(1.0f).put(0.0f).put(0.0f);
        fb.put(0.0f).put(1.0f);
        fb.put(1f).put(-1f);
        fb.put(0.0f).put(1.0f).put(0.0f);
        fb.put(1.0f).put(1.0f);
        fb.put(1f).put(1f);
        fb.put(0.0f).put(0.0f).put(1.0f);
        fb.put(1.0f).put(0.0f);
        fb.put(-1f).put(1f);
        fb.put(1.0f).put(1.0f).put(1.0f);
        fb.put(0.0f).put(0.0f);
        
        ByteBuffer indexBuffer = memAlloc(Float.BYTES * 6);
        IntBuffer ib = indexBuffer.asIntBuffer();
        ib.put(0);
        ib.put(1);
        ib.put(2);
        ib.put(2);
        ib.put(3);
        ib.put(0);
        
        VkBuffer vertexBufferObject = VkMemoryHelper.createDeviceLocalBuffer(logicalDevice.getHandle(),
        													physicalDevice.getMemoryProperties(),
        													logicalDevice.getTransferCommandPool().getHandle(),
        													logicalDevice.getTransferQueue(),
        													vertexBuffer, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);
        
        VkBuffer indexBufferObject = VkMemoryHelper.createDeviceLocalBuffer(logicalDevice.getHandle(),
        													physicalDevice.getMemoryProperties(),
        													logicalDevice.getTransferCommandPool().getHandle(),
        													logicalDevice.getTransferQueue(),
        													indexBuffer, VK_BUFFER_USAGE_INDEX_BUFFER_BIT);
	    
	    // Image
	    VkImage image = VkMemoryHelper.createImage(logicalDevice.getHandle(),
	    										 physicalDevice.getMemoryProperties(),
	    										 logicalDevice.getTransferCommandPool().getHandle(),
	    										 logicalDevice.getTransferQueue(),
	    										 "images/vulkan-logo.jpg");
		
		// image view
		VkImageView imageView = new VkImageView();
		imageView.createImageView(logicalDevice.getHandle(), VK_FORMAT_R8G8B8A8_UNORM, image.getHandle());
		
		// sampler
		VkSampler sampler = new VkSampler();
		sampler.create(logicalDevice.getHandle());
	    
		// descriptors
	    DescriptorSetLayout descriptorLayout = new DescriptorSetLayout(logicalDevice.getHandle(),1);
	    descriptorLayout.addLayoutBinding(0, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    								     VK_SHADER_STAGE_FRAGMENT_BIT);
	    descriptorLayout.create();
	    
	    DescriptorSet descriptorSet = new DescriptorSet(logicalDevice.getHandle(), 
	    					VkContext.getEnvironment().getDescriptorPool(DescriptorPoolType.COMBINED_IMAGE_SAMPLER).getHandle(),
	    					descriptorLayout.getHandle());
	    descriptorSet.updateDescriptorImageBuffer(imageView.getHandle(), sampler.getHandle(), 0);
	    
	    long[] descriptorSets = new long[2];
	    descriptorSets[0] = VkContext.getEnvironment().getDescriptorSet(DescriptorSetKey.CAMERA).getSet().getHandle();
	    descriptorSets[1] = descriptorSet.getHandle();
	    
	    LongBuffer descriptorSetLayouts = memAllocLong(2);
	    descriptorSetLayouts.put(VkContext.getEnvironment().getDescriptorSet(DescriptorSetKey.CAMERA).getLayout().getHandle());
	    descriptorSetLayouts.put(descriptorLayout.getHandle());
	    descriptorSetLayouts.flip();
	    
	    ShaderPipeline shaderPipeline = new ShaderPipeline();
	    shaderPipeline.createVertexShader(logicalDevice.getHandle(), "shaders/vert.spv");
	    shaderPipeline.createFragmentShader(logicalDevice.getHandle(), "shaders/frag.spv");
	    shaderPipeline.createShaderPipeline();
	    
	    RenderPass renderPass = new RenderPass();
	    renderPass.specifyAttachmentDescription(imageFormat);
	    renderPass.specifyAttachmentReference();
	    renderPass.specifySubpass();
	    renderPass.specifyDependency();
	    renderPass.createRenderPass(logicalDevice.getHandle());
	    
	    pipeline = new Pipeline();
	    VertexInputInfo vertexInputInfo = new VertexInputInfo();
	    vertexInputInfo.createBindingDescription(0, 3, 7 * 4);
	    vertexInputInfo.addVertexAttributeDescription(0, VK_FORMAT_R32G32_SFLOAT, 0);
	    vertexInputInfo.addVertexAttributeDescription(1, VK_FORMAT_R32G32B32_SFLOAT, 8);
	    vertexInputInfo.addVertexAttributeDescription(2, VK_FORMAT_R32G32_SFLOAT, 20);
	    
	    pipeline.setVertexInput(vertexInputInfo);
	    pipeline.setInputAssembly();
	    pipeline.setViewportAndScissor(swapExtent);
	    pipeline.setRasterizer();
	    pipeline.setMultisampling();
	    pipeline.setColorBlending();
	    pipeline.setDepthAndStencilTest();
	    pipeline.setDynamicState();
	    pipeline.setLayout(logicalDevice.getHandle(), descriptorSetLayouts);
	    pipeline.createPipeline(logicalDevice.getHandle(), shaderPipeline, renderPass);
	    
	    swapChain = new SwapChain(logicalDevice.getHandle(), 
	    						  surface, 
	    						  minImageCount, 
	    						  imageFormat, 
	    						  colorSpace, 
	    						  presentMode,
	    						  swapExtent,
	    						  renderPass.getHandle());
	    
	    swapChain.createRenderCommandBuffers(logicalDevice.getGraphicsCommandPool(),
	    									 pipeline.getHandle(),
	    									 pipeline.getLayoutHandle(),
	    									 renderPass.getHandle(), 
	    									 vertexBufferObject.getHandle(),
	    									 indexBufferObject.getHandle(),
	    									 6, descriptorSets);
	    swapChain.createSubmitInfo();
	}
    

	@Override
	public void render() {
		
		// wait for queues to be finished before start draw command
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
		pipeline.destroy(logicalDevice.getHandle());
		EngineContext.getCamera().shutdown();
		VkContext.getEnvironment().shutdown();
		logicalDevice.destroy();

		VkContext.getVulkanInstance().destroy();		
	}
	
}
