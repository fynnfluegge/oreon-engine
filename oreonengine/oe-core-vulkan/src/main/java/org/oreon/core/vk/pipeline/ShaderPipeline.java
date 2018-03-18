package org.oreon.core.vk.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

import lombok.Getter;

import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;

public class ShaderPipeline {

	@Getter
	private VkPipelineShaderStageCreateInfo.Buffer shaderPipeline;
	private List<ShaderModule> shaderStages = new ArrayList<ShaderModule>();

	public void createShaderPipeline(){
		
		shaderPipeline = VkPipelineShaderStageCreateInfo.calloc(shaderStages.size());
		
		for (ShaderModule shaderStage : shaderStages){
			shaderPipeline.put(shaderStage.getShaderStageInfo());
		}
		
		shaderPipeline.flip();
	}
	
	public void createVertexShader(VkDevice device, String filePath){
		
		shaderStages.add(new ShaderModule(device, filePath, VK_SHADER_STAGE_VERTEX_BIT));
	}
	
	public void createFragmentShader(VkDevice device, String filePath){
		
		shaderStages.add(new ShaderModule(device, filePath, VK_SHADER_STAGE_FRAGMENT_BIT));
	}
	
	public void destroy(VkDevice device){
		
		shaderPipeline.free();
		
		for (ShaderModule shaderModule : shaderStages){
			shaderModule.destroy(device);
		}
	}
	
}
