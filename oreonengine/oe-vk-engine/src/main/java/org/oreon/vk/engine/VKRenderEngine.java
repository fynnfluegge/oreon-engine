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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkDebugReportCallbackCreateInfoEXT;
import org.lwjgl.vulkan.VkDebugReportCallbackEXT;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.oreon.core.buffers.Framebuffer;
import org.oreon.core.math.Quaternion;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.system.RenderEngine;
import org.oreon.core.texture.Texture;
import org.oreon.core.vk.buffers.VertexBuffer;
import org.oreon.core.vk.buffers.VertexInputInfo;
import org.oreon.core.vk.device.LogicalDevice;
import org.oreon.core.vk.device.PhysicalDevice;
import org.oreon.core.vk.pipeline.Pipeline;
import org.oreon.core.vk.pipeline.PipelineLayout;
import org.oreon.core.vk.pipeline.RenderPass;
import org.oreon.core.vk.pipeline.ShaderPipeline;
import org.oreon.core.vk.swapchain.SwapChain;
import org.oreon.core.vk.util.DeviceCapabilities;
import org.oreon.core.vk.util.VKUtil;

public class VKRenderEngine implements RenderEngine{
	
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
        
        ppEnabledLayerNames = VKUtil.getValidationLayerNames(validationEnabled, layers);
        
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
	        throw new AssertionError("Failed to create surface: " + VKUtil.translateVulkanResult(err));
	    }
	    
        physicalDevice = new PhysicalDevice(vkInstance, surface);
        
        DeviceCapabilities.checkPhysicalDeviceProperties(physicalDevice.getHandle());
        DeviceCapabilities.checkPhysicalDeviceFeatures(physicalDevice.getHandle());
        
	    logicalDevice = new LogicalDevice();
	    logicalDevice.createGraphicsAndPresentationDevice(physicalDevice, 0, ppEnabledLayerNames);
	    
	    VkExtent2D swapExtent = physicalDevice.getSwapChainCapabilities().getSurfaceCapabilities().currentExtent();
	    swapExtent.width(1280);
	    swapExtent.height(720);
	    
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
	    
	    ShaderPipeline shaderPipeline = new ShaderPipeline();
	    shaderPipeline.createVertexShader(logicalDevice.getHandle(), "shaders/triangle.vert.spv");
	    shaderPipeline.createFragmentShader(logicalDevice.getHandle(), "shaders/triangle.frag.spv");
	    shaderPipeline.createShaderPipeline();
	    
	    PipelineLayout pipeLineLayout = new PipelineLayout();
	    pipeLineLayout.specifyPipelineLayout();
	    pipeLineLayout.createPipelineLayout(logicalDevice.getHandle());
	    
	    RenderPass renderPass = new RenderPass();
	    renderPass.specifyAttachmentDescription(imageFormat);
	    renderPass.specifyAttachmentReference();
	    renderPass.specifySubpass();
	    renderPass.specifyDependency();
	    renderPass.createRenderPass(logicalDevice.getHandle());
	    
	    ByteBuffer buffer = memAlloc(3 * 2 * 4 + 3 * 3 * 4);
        FloatBuffer fb = buffer.asFloatBuffer();
        fb.put(-0.5f).put(-0.5f);
        fb.put(1.0f).put(0.0f).put(0.0f);
        fb.put( 0.5f).put(-0.5f);
        fb.put(0.0f).put(1.0f).put(0.0f);
        fb.put( 0.0f).put( 0.5f);
        fb.put(1.0f).put(0.0f).put(1.0f);
	    
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
	    pipeline.createPipeline(logicalDevice.getHandle(), shaderPipeline, renderPass, pipeLineLayout);
	    
	    VertexBuffer vertexBuffer = new VertexBuffer();
	    
	    vertexBuffer.create(logicalDevice.getHandle(), buffer);
	    vertexBuffer.allocate(logicalDevice.getHandle(), physicalDevice.getMemoryProperties(), buffer);
	    
	    swapChain = new SwapChain(logicalDevice.getHandle(), 
	    						  surface, 
	    						  minImageCount, 
	    						  imageFormat, 
	    						  colorSpace, 
	    						  presentMode,
	    						  swapExtent,
	    						  renderPass.getHandle());
	    
	    swapChain.createCommandPool(logicalDevice.getHandle(), logicalDevice.getGraphicsAndPresentationQueueFamilyIndex());
	    swapChain.createRenderCommandBuffers(logicalDevice.getHandle(), pipeline.getHandle(), renderPass.getHandle(), vertexBuffer.getHandle());
	    swapChain.createSubmitInfo();
	}
    

	@Override
	public void render() {
		
		// wait for queues to be finished before start draw command
		vkQueueWaitIdle(logicalDevice.getGraphicsAndPresentationQueue());
		
		swapChain.draw(logicalDevice.getHandle(), logicalDevice.getGraphicsAndPresentationQueue());
	}

	@Override
	public void update() {
		
	}

	@Override
	public void shutdown() {
		
		// wait for queues to be finished before destroy vulkan objects
		vkQueueWaitIdle(logicalDevice.getGraphicsAndPresentationQueue());
		
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
                .pApplicationName(memUTF8("GLFW Vulkan Demo"))
                .pEngineName(memUTF8(""))
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
            throw new AssertionError("Failed to create VkInstance: " + VKUtil.translateVulkanResult(err));
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
		
        VkDebugReportCallbackCreateInfoEXT dbgCreateInfo = VkDebugReportCallbackCreateInfoEXT.calloc()
                .sType(VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT)
                .pNext(0)
                .pfnCallback(callback)
                .pUserData(0)
                .flags(flags);
        
        LongBuffer pCallback = memAllocLong(1);
        int err = vkCreateDebugReportCallbackEXT(instance, dbgCreateInfo, null, pCallback);
        long callbackHandle = pCallback.get(0);
        
        if (err != VK_SUCCESS) {
            throw new AssertionError("Failed to create VkInstance: " + VKUtil.translateVulkanResult(err));
        }
        
        memFree(pCallback);
        dbgCreateInfo.free();
        
        return callbackHandle;
    }
	
	@Override
	public boolean isGrid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCameraUnderWater() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWaterReflection() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWaterRefraction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBloomEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Framebuffer getMultisampledFbo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Framebuffer getDeferredFbo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Texture getSceneDepthmap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quaternion getClipplane() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getSightRangeFactor() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getUnderwater() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setClipplane(Quaternion plane) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setGrid(boolean flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWaterRefraction(boolean flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWaterReflection(boolean flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCameraUnderWater(boolean flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSightRangeFactor(float range) {
		// TODO Auto-generated method stub
		
	}

}
