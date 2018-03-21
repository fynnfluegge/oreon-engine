package org.oreon.vk.engine;

import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.vulkan.EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME;
import static org.lwjgl.vulkan.EXTDebugReport.VK_DEBUG_REPORT_ERROR_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugReport.VK_DEBUG_REPORT_WARNING_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugReport.VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT;
import static org.lwjgl.vulkan.EXTDebugReport.vkDestroyDebugReportCallbackEXT;
import static org.lwjgl.vulkan.EXTDebugReport.vkCreateDebugReportCallbackEXT;
import static org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_FIFO_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_UNORM;
import static org.lwjgl.vulkan.VK10.vkCreateInstance;
import static org.lwjgl.vulkan.VK10.vkDestroyInstance;
import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;
import static org.lwjgl.vulkan.VK10.VK_MAKE_VERSION;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkDebugReportCallbackCreateInfoEXT;
import org.lwjgl.vulkan.VkDebugReportCallbackEXT;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkSubmitInfo;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.RenderEngine;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.vk.buffers.VkBuffer;
import org.oreon.core.vk.buffers.VkUniformBuffer;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.context.VkCamera;
import org.oreon.core.vk.descriptor.DescriptorPool;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.device.LogicalDevice;
import org.oreon.core.vk.device.PhysicalDevice;
import org.oreon.core.vk.image.VkImageLoader;
import org.oreon.core.vk.pipeline.Pipeline;
import org.oreon.core.vk.pipeline.RenderPass;
import org.oreon.core.vk.pipeline.ShaderPipeline;
import org.oreon.core.vk.pipeline.VertexInputInfo;
import org.oreon.core.vk.swapchain.SwapChain;
import org.oreon.core.vk.util.DeviceCapabilities;
import org.oreon.core.vk.util.VkUtil;

public class VkRenderEngine implements RenderEngine{
	
	private VkInstance vkInstance;
	private PhysicalDevice physicalDevice;
	private LogicalDevice logicalDevice;
	private SwapChain swapChain;
	private long surface;
	
	private Pipeline pipeline;
	
	private VkUniformBuffer uniformBuffer;
	
	private ByteBuffer[] layers = {
	            	memUTF8("VK_LAYER_LUNARG_standard_validation"),
				};
	private final boolean validationEnabled = Boolean.parseBoolean(System.getProperty("vulkan.validation", "true"));
	private PointerBuffer ppEnabledLayerNames;
	
	private long debugCallbackHandle;
	
	@Override
	public void init() {
		
		if (!glfwVulkanSupported()) {
	            throw new AssertionError("GLFW failed to find the Vulkan loader");
	        }
		
		PointerBuffer requiredExtensions = glfwGetRequiredInstanceExtensions();
        if (requiredExtensions == null) {
            throw new AssertionError("Failed to find list of required Vulkan extensions");
        }
        
        ppEnabledLayerNames = VkUtil.getValidationLayerNames(validationEnabled, layers);
        
        vkInstance = createVkInstance(requiredExtensions, ppEnabledLayerNames); 
        
        VkDebugReportCallbackEXT debugCallback = new VkDebugReportCallbackEXT() {
            public int invoke(int flags, int objectType, long object, long location, int messageCode, long pLayerPrefix, long pMessage, long pUserData) {
                System.err.println("ERROR OCCURED: " + VkDebugReportCallbackEXT.getString(pMessage));
                return 0;
            }
        };
        
        debugCallbackHandle = setupDebugging(vkInstance, VK_DEBUG_REPORT_ERROR_BIT_EXT | VK_DEBUG_REPORT_WARNING_BIT_EXT, debugCallback);
        
        LongBuffer pSurface = memAllocLong(1);
	    int err = glfwCreateWindowSurface(vkInstance, CoreSystem.getInstance().getWindow().getId(), null, pSurface);
	    
	    surface = pSurface.get(0);
	    if (err != VK_SUCCESS) {
	        throw new AssertionError("Failed to create surface: " + VkUtil.translateVulkanResult(err));
	    }
	    
        physicalDevice = new PhysicalDevice(vkInstance, surface);
        
	    logicalDevice = new LogicalDevice();
	    logicalDevice.createDevice(physicalDevice, 0, ppEnabledLayerNames);
	    
	    VkExtent2D swapExtent = physicalDevice.getSwapChainCapabilities().getSurfaceCapabilities().currentExtent();
	    swapExtent.width(CoreSystem.getInstance().getWindow().getWidth());
	    swapExtent.height(CoreSystem.getInstance().getWindow().getHeight());
	    
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
	    
	    ByteBuffer vertexBuffer = memAlloc(4 * 2 * 4 + 4 * 3 * 4);
        FloatBuffer fb = vertexBuffer.asFloatBuffer();
        fb.put(-0.5f).put(-0.5f);
        fb.put(1.0f).put(0.0f).put(0.0f);
        fb.put( 0.5f).put(-0.5f);
        fb.put(0.0f).put(1.0f).put(0.0f);
        fb.put(0.5f).put( 0.5f);
        fb.put(0.0f).put(0.0f).put(1.0f);
        fb.put(-0.5f).put( 0.5f);
        fb.put(1.0f).put(1.0f).put(1.0f);
        
        VkBuffer vertexBufferObject = new VkBuffer();
	    
	    VkBuffer stagingVertexBufferObject = new VkBuffer();
	    
	    stagingVertexBufferObject.create(logicalDevice.getHandle(), vertexBuffer.limit(), VK_BUFFER_USAGE_TRANSFER_SRC_BIT);
	    stagingVertexBufferObject.allocate(logicalDevice.getHandle(), physicalDevice.getMemoryProperties(),
	    				       VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
	    stagingVertexBufferObject.mapMemory(logicalDevice.getHandle(), vertexBuffer);
	    

	    vertexBufferObject.create(logicalDevice.getHandle(), vertexBuffer.limit(),
							VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);
	    vertexBufferObject.allocate(logicalDevice.getHandle(), physicalDevice.getMemoryProperties(),
				  			  VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
	    
	    CommandBuffer vertexCopyCommandBuffer = new CommandBuffer(logicalDevice.getHandle(),
	    												logicalDevice.getTransferCommandPool().getHandle());
	    vertexCopyCommandBuffer.beginRecord(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
	    vertexCopyCommandBuffer.recordTransferPass(stagingVertexBufferObject.getHandle(), vertexBufferObject.getHandle(), 0, 0, vertexBuffer.limit());
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
	    stagingIndexBufferObject.allocate(logicalDevice.getHandle(), physicalDevice.getMemoryProperties(),
	    				       VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
	    stagingIndexBufferObject.mapMemory(logicalDevice.getHandle(), indexBuffer);
	    
	    indexBufferObject.create(logicalDevice.getHandle(), indexBuffer.limit(),
				VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_INDEX_BUFFER_BIT);
	    indexBufferObject.allocate(logicalDevice.getHandle(), physicalDevice.getMemoryProperties(),
	  			  VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
	    
	    CommandBuffer indexCopyCommandBuffer = new CommandBuffer(logicalDevice.getHandle(),
																  logicalDevice.getTransferCommandPool().getHandle());
	    indexCopyCommandBuffer.beginRecord(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
	    indexCopyCommandBuffer.recordTransferPass(stagingIndexBufferObject.getHandle(), indexBufferObject.getHandle(), 0, 0, vertexBuffer.limit());
	    indexCopyCommandBuffer.finishRecord();
	    VkSubmitInfo submitInfo1 = indexCopyCommandBuffer.createSubmitInfo(null, null, null);
	    indexCopyCommandBuffer.submit(logicalDevice.getTransferQueue(), submitInfo1);
	    vkQueueWaitIdle(logicalDevice.getTransferQueue());
	    
	    indexCopyCommandBuffer.destroy(logicalDevice.getHandle(), logicalDevice.getTransferCommandPool().getHandle());
	    stagingIndexBufferObject.destroy(logicalDevice.getHandle());
	    
	    // Camera UBO
	    ByteBuffer cameraBuffer = memAlloc(4 * 16);
	    FloatBuffer cameraMatrix = cameraBuffer.asFloatBuffer();
	    VkCamera camera = (VkCamera) CoreSystem.getInstance().getScenegraph().getCamera();
	    cameraMatrix.put(BufferUtil.createFlippedBuffer(camera.getViewProjectionMatrix()));
	    
	    uniformBuffer = new VkUniformBuffer(logicalDevice.getHandle(),
	    									physicalDevice.getMemoryProperties(),
	    									cameraBuffer);
	    
	    // Image
	    VkImageLoader.loadImage("images/vulkan-logo.jpg");
	    
	    DescriptorSetLayout descriptorLayout = new DescriptorSetLayout(1);
	    descriptorLayout.setLayoutBinding();
	    descriptorLayout.create(logicalDevice.getHandle());
	    
	    DescriptorPool descriptorPool = new DescriptorPool(logicalDevice.getHandle());
	    
	    DescriptorSet descriptorSet = new DescriptorSet(logicalDevice.getHandle(), 
	    												descriptorPool.getHandle(),
	    												descriptorLayout.getPHandle());
	    descriptorSet.configureWrite(logicalDevice.getHandle(), uniformBuffer.getHandle(), 0, cameraBuffer.limit());
	    
	    long[] descriptorSets = new long[1];
	    descriptorSets[0] = descriptorSet.getHandle();
	    
	    ShaderPipeline shaderPipeline = new ShaderPipeline();
	    shaderPipeline.createVertexShader(logicalDevice.getHandle(), "shaders/triangle.vert.spv");
	    shaderPipeline.createFragmentShader(logicalDevice.getHandle(), "shaders/triangle.frag.spv");
	    shaderPipeline.createShaderPipeline();
	    
	    RenderPass renderPass = new RenderPass();
	    renderPass.specifyAttachmentDescription(imageFormat);
	    renderPass.specifyAttachmentReference();
	    renderPass.specifySubpass();
	    renderPass.specifyDependency();
	    renderPass.createRenderPass(logicalDevice.getHandle());
	    
	    pipeline = new Pipeline();
	    VertexInputInfo vertexInputInfo = new VertexInputInfo();
	    vertexInputInfo.createBindingDescription(5 * 4);
	    vertexInputInfo.createAttributeDescription();
	    
	    pipeline.specifyVertexInput(vertexInputInfo);
	    pipeline.specifyInputAssembly();
	    pipeline.specifyViewportAndScissor(swapExtent);
	    pipeline.specifyRasterizer();
	    pipeline.specifyMultisampling();
	    pipeline.specifyColorBlending();
	    pipeline.specifyDepthAndStencilTest();
	    pipeline.specifyDynamicState();
	    pipeline.specifyLayout(logicalDevice.getHandle(), descriptorLayout.getPHandle());
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
		
		 VkCamera camera = (VkCamera) CoreSystem.getInstance().getScenegraph().getCamera();
		 
		 ByteBuffer cameraBuffer = memAlloc(4 * 16);
		 FloatBuffer cameraMatrix = cameraBuffer.asFloatBuffer();
		 cameraMatrix.put(BufferUtil.createFlippedBuffer(camera.getViewProjectionMatrix()));
		 uniformBuffer.updateData(logicalDevice.getHandle(), cameraBuffer);
	}

	@Override
	public void shutdown() {
		
		// wait for queues to be finished before destroy vulkan objects
		vkQueueWaitIdle(logicalDevice.getGraphicsQueue());
		
		vkDestroySwapchainKHR(logicalDevice.getHandle(), swapChain.getHandle(), null);
		swapChain.destroy(logicalDevice.getHandle());
		pipeline.destroy(logicalDevice.getHandle());
		logicalDevice.destroy();
		vkDestroyDebugReportCallbackEXT(vkInstance, debugCallbackHandle, null);
        vkDestroyInstance(vkInstance, null);		
	}
	
	private VkInstance createVkInstance(PointerBuffer requiredExtensions, PointerBuffer enabledLayerNames) {
		
        VkApplicationInfo appInfo = VkApplicationInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                .pApplicationName(memUTF8("Vulkan Demo"))
                .pEngineName(memUTF8("OREON ENGINE"))
                .apiVersion(VK_MAKE_VERSION(1, 0, 2));
        
        ByteBuffer VK_EXT_DEBUG_REPORT_EXTENSION = memUTF8(VK_EXT_DEBUG_REPORT_EXTENSION_NAME);
        
        // +1 due to VK_EXT_DEBUG_REPORT_EXTENSION
        PointerBuffer ppEnabledExtensionNames = memAllocPointer(requiredExtensions.remaining() + 1);
        ppEnabledExtensionNames.put(requiredExtensions);
        ppEnabledExtensionNames.put(VK_EXT_DEBUG_REPORT_EXTENSION);
        ppEnabledExtensionNames.flip();
        
        DeviceCapabilities.checkInstanceExtensionSupport(ppEnabledExtensionNames);
        
        VkInstanceCreateInfo pCreateInfo = VkInstanceCreateInfo.calloc()
                .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                .pNext(0)
                .pApplicationInfo(appInfo)
                .ppEnabledExtensionNames(ppEnabledExtensionNames)
                .ppEnabledLayerNames(enabledLayerNames);
        PointerBuffer pInstance = memAllocPointer(1);
        int err = vkCreateInstance(pCreateInfo, null, pInstance);
        long instance = pInstance.get(0);
    
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create VkInstance: " + VkUtil.translateVulkanResult(err));
        }
        VkInstance ret = new VkInstance(instance, pCreateInfo);
        
        pCreateInfo.free();
        memFree(pInstance);
        memFree(VK_EXT_DEBUG_REPORT_EXTENSION);
        memFree(ppEnabledExtensionNames);
        memFree(appInfo.pApplicationName());
        memFree(appInfo.pEngineName());
        appInfo.free();
        
        return ret;
    }
	
	private long setupDebugging(VkInstance instance, int flags, VkDebugReportCallbackEXT callback) {
		
        VkDebugReportCallbackCreateInfoEXT debugCreateInfo = VkDebugReportCallbackCreateInfoEXT.calloc()
                .sType(VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT)
                .pNext(0)
                .pfnCallback(callback)
                .pUserData(0)
                .flags(flags);
        
        LongBuffer pCallback = memAllocLong(1);
        int err = vkCreateDebugReportCallbackEXT(instance, debugCreateInfo, null, pCallback);
        long callbackHandle = pCallback.get(0);
        
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create VkInstance: " + VkUtil.translateVulkanResult(err));
        }
        
        memFree(pCallback);
        debugCreateInfo.free();
        
        return callbackHandle;
    }

}
