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
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER;
import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32B32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.oreon.core.context.EngineContext;
import org.oreon.core.system.RenderEngine;
import org.oreon.core.vk.core.buffers.VkBuffer;
import org.oreon.core.vk.core.command.CommandBuffer;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.context.VulkanInstance;
import org.oreon.core.vk.core.descriptor.DescriptorPool;
import org.oreon.core.vk.core.descriptor.DescriptorSet;
import org.oreon.core.vk.core.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.core.device.LogicalDevice;
import org.oreon.core.vk.core.device.PhysicalDevice;
import org.oreon.core.vk.core.image.VkImage;
import org.oreon.core.vk.core.image.VkImageLoader;
import org.oreon.core.vk.core.image.VkImageView;
import org.oreon.core.vk.core.image.VkSampler;
import org.oreon.core.vk.core.pipeline.Pipeline;
import org.oreon.core.vk.core.pipeline.RenderPass;
import org.oreon.core.vk.core.pipeline.ShaderPipeline;
import org.oreon.core.vk.core.pipeline.VertexInputInfo;
import org.oreon.core.vk.core.swapchain.SwapChain;
import org.oreon.core.vk.core.util.VkUtil;

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
        VkContext.registerInstance(vulkanInstance);
        
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
	    
	    VkContext.registerPhysicalDevice(physicalDevice);
	    VkContext.registerLogicalDevice(logicalDevice);
	    
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
        fb.put(-0.5f).put(-0.5f);
        fb.put(1.0f).put(0.0f).put(0.0f);
        fb.put(0.0f).put(1.0f);
        
        fb.put(0.5f).put(-0.5f);
        fb.put(0.0f).put(1.0f).put(0.0f);
        fb.put(1.0f).put(1.0f);
        
        fb.put(0.5f).put( 0.5f);
        fb.put(0.0f).put(0.0f).put(1.0f);
        fb.put(1.0f).put(0.0f);
        
        fb.put(-0.5f).put( 0.5f);
        fb.put(1.0f).put(1.0f).put(1.0f);
        fb.put(0.0f).put(0.0f);
        
        VkBuffer vertexBufferObject = new VkBuffer();
	    
	    VkBuffer stagingVertexBufferObject = new VkBuffer();
	    
	    stagingVertexBufferObject.create(logicalDevice.getHandle(), vertexBuffer.limit(), VK_BUFFER_USAGE_TRANSFER_SRC_BIT);
	    stagingVertexBufferObject.allocateBuffer(logicalDevice.getHandle(), physicalDevice.getMemoryProperties(),
	    				       VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
	    stagingVertexBufferObject.bindBufferMemory(logicalDevice.getHandle());
	    stagingVertexBufferObject.mapMemory(logicalDevice.getHandle(), vertexBuffer);
	    

	    vertexBufferObject.create(logicalDevice.getHandle(), vertexBuffer.limit(),
							VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);
	    vertexBufferObject.allocateBuffer(logicalDevice.getHandle(), physicalDevice.getMemoryProperties(),
				  			  VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
	    vertexBufferObject.bindBufferMemory(logicalDevice.getHandle());
	    
	    CommandBuffer vertexCopyCommandBuffer = new CommandBuffer(logicalDevice.getHandle(),
	    												logicalDevice.getTransferCommandPool().getHandle());
	    vertexCopyCommandBuffer.beginRecord(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
	    vertexCopyCommandBuffer.recordCopyBufferCmd(stagingVertexBufferObject.getHandle(), vertexBufferObject.getHandle(), 0, 0, vertexBuffer.limit());
	    vertexCopyCommandBuffer.finishRecord();
	    VkSubmitInfo submitInfo0 = vertexCopyCommandBuffer.createSubmitInfo(null, null, null);
	    vertexCopyCommandBuffer.submit(logicalDevice.getTransferQueue(), submitInfo0);
	    vkQueueWaitIdle(logicalDevice.getTransferQueue());
	    
	    vertexCopyCommandBuffer.destroy(logicalDevice.getHandle(), logicalDevice.getTransferCommandPool().getHandle());
	    stagingVertexBufferObject.destroy(logicalDevice.getHandle());
	    
	    ByteBuffer indexBuffer = memAlloc(4 * 6);
        IntBuffer ib = indexBuffer.asIntBuffer();
        ib.put(0);
        ib.put(1);
        ib.put(2);
        ib.put(2);
        ib.put(3);
        ib.put(0);
        
        VkBuffer indexBufferObject = new VkBuffer();
        
        VkBuffer stagingIndexBufferObject = new VkBuffer();
        
        stagingIndexBufferObject.create(logicalDevice.getHandle(), indexBuffer.limit(), VK_BUFFER_USAGE_TRANSFER_SRC_BIT);
	    stagingIndexBufferObject.allocateBuffer(logicalDevice.getHandle(), physicalDevice.getMemoryProperties(),
	    				       VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
	    stagingIndexBufferObject.bindBufferMemory(logicalDevice.getHandle());
	    stagingIndexBufferObject.mapMemory(logicalDevice.getHandle(), indexBuffer);
	    
	    indexBufferObject.create(logicalDevice.getHandle(), indexBuffer.limit(),
				VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_INDEX_BUFFER_BIT);
	    indexBufferObject.allocateBuffer(logicalDevice.getHandle(), physicalDevice.getMemoryProperties(),
	  			  VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
	    indexBufferObject.bindBufferMemory(logicalDevice.getHandle());
	    
	    CommandBuffer indexCopyCommandBuffer = new CommandBuffer(logicalDevice.getHandle(),
																  logicalDevice.getTransferCommandPool().getHandle());
	    indexCopyCommandBuffer.beginRecord(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
	    indexCopyCommandBuffer.recordCopyBufferCmd(stagingIndexBufferObject.getHandle(), indexBufferObject.getHandle(), 0, 0, vertexBuffer.limit());
	    indexCopyCommandBuffer.finishRecord();
	    VkSubmitInfo submitInfo1 = indexCopyCommandBuffer.createSubmitInfo(null, null, null);
	    indexCopyCommandBuffer.submit(logicalDevice.getTransferQueue(), submitInfo1);
	    vkQueueWaitIdle(logicalDevice.getTransferQueue());
	    
	    indexCopyCommandBuffer.destroy(logicalDevice.getHandle(), logicalDevice.getTransferCommandPool().getHandle());
	    stagingIndexBufferObject.destroy(logicalDevice.getHandle());
	    
	    // Image
	    ByteBuffer imageBuffer = VkImageLoader.loadImage("images/vulkan-logo.jpg");
	    
	    VkBuffer imageStagingBuffer = new VkBuffer();
	    imageStagingBuffer.create(logicalDevice.getHandle(), imageBuffer.limit(),
	    						  VK_BUFFER_USAGE_TRANSFER_SRC_BIT);
	    imageStagingBuffer.allocateBuffer(logicalDevice.getHandle(),
	    								  physicalDevice.getMemoryProperties(),
	    								  VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
	    imageStagingBuffer.bindBufferMemory(logicalDevice.getHandle());
	    imageStagingBuffer.mapMemory(logicalDevice.getHandle(), imageBuffer);
	    
	    VkImage image = new VkImage();
	    image.create(logicalDevice.getHandle(), 512, 512, 1, VK_FORMAT_R8G8B8A8_UNORM,
	    			 VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT);
	    image.allocate(logicalDevice.getHandle(), physicalDevice.getMemoryProperties(),
	    			   VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
	    image.bindImageMemory(logicalDevice.getHandle());
	    
	    // transition layout
	    CommandBuffer transitionImageCommandBuffer = new CommandBuffer(logicalDevice.getHandle(),
	    												  			   logicalDevice.getTransferCommandPool().getHandle());
	    transitionImageCommandBuffer.beginRecord(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
	    transitionImageCommandBuffer.recordImageLayoutTransitionCmd(image.getHandle(),
	    						VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL);
	    transitionImageCommandBuffer.finishRecord();
	    VkSubmitInfo submitInfo2 = transitionImageCommandBuffer.createSubmitInfo(null, null, null);
	    transitionImageCommandBuffer.submit(logicalDevice.getTransferQueue(), submitInfo2);
	    vkQueueWaitIdle(logicalDevice.getTransferQueue());
	    
	    // copy buffer to image
	    CommandBuffer imageCopyCommandBuffer = new CommandBuffer(logicalDevice.getHandle(),
				  										         logicalDevice.getTransferCommandPool().getHandle());
	    imageCopyCommandBuffer.beginRecord(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
	    imageCopyCommandBuffer.recordCopyBufferToImageCmd(imageStagingBuffer.getHandle(), image.getHandle(),
	    												  512, 512, 1);
	    imageCopyCommandBuffer.finishRecord();
	    VkSubmitInfo submitInfo3 = imageCopyCommandBuffer.createSubmitInfo(null, null, null);
	    imageCopyCommandBuffer.submit(logicalDevice.getTransferQueue(), submitInfo3);
	    vkQueueWaitIdle(logicalDevice.getTransferQueue());
	    
	    // transition layout
	    CommandBuffer transitionImageCommandBuffer2 = new CommandBuffer(logicalDevice.getHandle(),
			  			   											   logicalDevice.getTransferCommandPool().getHandle());
		transitionImageCommandBuffer2.beginRecord(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
		transitionImageCommandBuffer2.recordImageLayoutTransitionCmd(image.getHandle(),
							VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
							VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
		transitionImageCommandBuffer2.finishRecord();
		VkSubmitInfo submitInfo4 = transitionImageCommandBuffer2.createSubmitInfo(null, null, null);
		transitionImageCommandBuffer.submit(logicalDevice.getTransferQueue(), submitInfo4);
		vkQueueWaitIdle(logicalDevice.getTransferQueue());
		
		// image view
		VkImageView imageView = new VkImageView();
		imageView.createImageView(logicalDevice.getHandle(), VK_FORMAT_R8G8B8A8_UNORM, image.getHandle());
		
		// sampler
		VkSampler sampler = new VkSampler();
		sampler.create(logicalDevice.getHandle());
	    
		// descriptors
	    DescriptorSetLayout descriptorLayout = new DescriptorSetLayout(2);
	    descriptorLayout.addLayoutBinding(0,VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
	    								  VK_SHADER_STAGE_VERTEX_BIT);
	    descriptorLayout.addLayoutBinding(1, VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
	    								  VK_SHADER_STAGE_FRAGMENT_BIT);
	    descriptorLayout.create(logicalDevice.getHandle());
	    
	    DescriptorPool descriptorPool = new DescriptorPool(2);
	    descriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
	    descriptorPool.addPoolSize(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER);
	    descriptorPool.create(logicalDevice.getHandle());
	    
	    DescriptorSet descriptorSet = new DescriptorSet(logicalDevice.getHandle(), 
	    												descriptorPool.getHandle(),
	    												descriptorLayout.getPHandle());
	    descriptorSet.updateDescriptorBuffer(logicalDevice.getHandle(), VkContext.getVkCamera().getUniformBuffer().getHandle(), 4 * 16, 0, 0);
	    descriptorSet.updateDescriptorImageBuffer(logicalDevice.getHandle(), imageView.getHandle(), sampler.getHandle(), 1);
	    
	    long[] descriptorSets = new long[1];
	    descriptorSets[0] = descriptorSet.getHandle();
	    
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
	    pipeline.setLayout(logicalDevice.getHandle(), descriptorLayout.getPHandle());
	    pipeline.createPipeline(logicalDevice.getHandle(), shaderPipeline, renderPass);
	    
	    swapChain = new SwapChain(logicalDevice.getHandle(), 
	    						  surface, 
	    						  minImageCount, 
	    						  imageFormat, 
	    						  colorSpace, 
	    						  presentMode,
	    						  swapExtent,
	    						  renderPass.getHandle());
	    
	    swapChain.createRenderCommandBuffers(logicalDevice.getHandle(),
	    									 logicalDevice.getGraphicsCommandPool(),
	    									 pipeline.getHandle(),
	    									 pipeline.getLayoutHandle(),
	    									 renderPass.getHandle(), 
	    									 vertexBufferObject.getHandle(),
	    									 indexBufferObject.getHandle(),
	    									 descriptorSets);
	    swapChain.createSubmitInfo();
	}
    

	@Override
	public void render() {
		
		// wait for queues to be finished before start draw command
		vkQueueWaitIdle(logicalDevice.getGraphicsQueue());
		
		swapChain.draw(logicalDevice.getHandle(), logicalDevice.getGraphicsQueue());
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
		
		vkDestroySwapchainKHR(logicalDevice.getHandle(), swapChain.getHandle(), null);
		swapChain.destroy(logicalDevice.getHandle());
		pipeline.destroy(logicalDevice.getHandle());
		logicalDevice.destroy();

		VkContext.getVulkanInstance().destroy();		
	}
	
}
