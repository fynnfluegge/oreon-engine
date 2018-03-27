package org.oreon.core.vk.core.pipeline;

import static org.lwjgl.vulkan.VK10.VK_VERTEX_INPUT_RATE_VERTEX;

import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

import lombok.Getter;

public class VertexInputInfo {

	@Getter
	private VkVertexInputBindingDescription.Buffer bindingDescription;
	@Getter
	private VkVertexInputAttributeDescription.Buffer attributeDescriptions;
	
	private int binding;
	
	public void createBindingDescription(int binding, int attributeCount, int stride){
		
		this.binding = binding;
		
		bindingDescription = VkVertexInputBindingDescription.calloc(1)
                .binding(binding)
                .stride(stride)
                .inputRate(VK_VERTEX_INPUT_RATE_VERTEX);
		
		attributeDescriptions = VkVertexInputAttributeDescription.calloc(attributeCount);
	}
	
	public void addVertexAttributeDescription(int location, int format, int offset){
		
		VkVertexInputAttributeDescription attributeDescription = VkVertexInputAttributeDescription.calloc()
                .binding(binding)
                .location(location)
                .format(format)
                .offset(offset);
		
		attributeDescriptions.put(location, attributeDescription);
	}

}
