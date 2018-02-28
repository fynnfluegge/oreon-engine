package org.oreon.core.vk.buffers;

import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_VERTEX_INPUT_RATE_VERTEX;

import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

public class VertexInputInfo {

	private VkVertexInputBindingDescription.Buffer bindingDescription;
	private VkVertexInputAttributeDescription.Buffer attributeDescription;
	
	public void createBindingDescription(){
		
		bindingDescription = VkVertexInputBindingDescription.calloc(1)
                .binding(0)
                .stride(2 * 4)
                .inputRate(VK_VERTEX_INPUT_RATE_VERTEX);
	}
	
	public void createAttributeDescription(){
		
		attributeDescription = VkVertexInputAttributeDescription.calloc(1)
	                .binding(0)
	                .location(0)
	                .format(VK_FORMAT_R32G32_SFLOAT)
	                .offset(0);
	}

	public VkVertexInputBindingDescription.Buffer getBindingDescription() {
		return bindingDescription;
	}

	public VkVertexInputAttributeDescription.Buffer getAttributeDescription() {
		return attributeDescription;
	}

}
