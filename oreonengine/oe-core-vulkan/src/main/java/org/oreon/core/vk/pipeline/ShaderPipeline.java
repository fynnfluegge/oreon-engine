package org.oreon.core.vk.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT;

public class ShaderPipeline {

	private VkPipelineShaderStageCreateInfo.Buffer shaderPipeline;
	private List<ShaderStage> shaderStages = new ArrayList<ShaderStage>();

	public void createShaderPipeline(){
		
		shaderPipeline = VkPipelineShaderStageCreateInfo.calloc(shaderStages.size());
		
		for (ShaderStage shaderStage : shaderStages){
			shaderPipeline.put(shaderStage.getShaderStageInfo());
		}
		
		shaderPipeline.flip();
	}
	
	public void createVertexShader(VkDevice device, String filePath){
		
		shaderStages.add(new ShaderStage(device, filePath, VK_SHADER_STAGE_VERTEX_BIT));
	}
	
	public void createFragmentShader(VkDevice device, String filePath){
		
		shaderStages.add(new ShaderStage(device, filePath, VK_SHADER_STAGE_FRAGMENT_BIT));
	}
	
	public VkPipelineShaderStageCreateInfo.Buffer getShaderPipeline() {
		return shaderPipeline;
	}
	
}
