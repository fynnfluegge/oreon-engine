package org.oreon.core.vk.pipeline;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateShaderModule;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;
import org.oreon.core.util.ResourceLoader;
import org.oreon.core.vk.util.VKUtil;

public class ShaderStage {

	private VkPipelineShaderStageCreateInfo shaderStageInfo;
	
	public ShaderStage(VkDevice device, String filePath, int stage) {
		
		long shaderModule = createShaderModule(filePath, device);
		shaderStageInfo = createShaderStage(shaderModule, device, stage);
	}
	
	public long createShaderModule(String filePath, VkDevice device) {
		
		ByteBuffer shaderCode = null;
		try {
			shaderCode = ResourceLoader.ioResourceToByteBuffer(filePath, 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    int err;
	    VkShaderModuleCreateInfo moduleCreateInfo = VkShaderModuleCreateInfo.calloc()
	            .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
	            .pNext(0)
	            .pCode(shaderCode)
	            .flags(0);
	    LongBuffer pShaderModule = memAllocLong(1);
	    err = vkCreateShaderModule(device, moduleCreateInfo, null, pShaderModule);
	    long shaderModule = pShaderModule.get(0);
	   
	    if (err != VK_SUCCESS) {
	        throw new AssertionError("Failed to create shader module: " + VKUtil.translateVulkanResult(err));
	    }
	    
	    memFree(pShaderModule);
	    moduleCreateInfo.free();
	    memFree(shaderCode);
	    
	    return shaderModule;
	}
	
	public VkPipelineShaderStageCreateInfo createShaderStage(long module, VkDevice device, int stage){
		
		 VkPipelineShaderStageCreateInfo shaderStage = VkPipelineShaderStageCreateInfo.calloc()
	                .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
	                .stage(stage)
	                .module(module)
	                .pName(memUTF8("main"))
	                .pSpecializationInfo(null);
	       
		 return shaderStage;
	}

	public VkPipelineShaderStageCreateInfo getShaderStageInfo() {
		return shaderStageInfo;
	}

}
