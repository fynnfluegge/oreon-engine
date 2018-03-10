package org.oreon.core.vk.buffers;

import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R32G32B32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_VERTEX_INPUT_RATE_VERTEX;

import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

public class VertexInputInfo {

	private VkVertexInputBindingDescription.Buffer bindingDescription;
	private VkVertexInputAttributeDescription.Buffer attributeDescription;
	
	public void createBindingDescription(int stride){
		
		bindingDescription = VkVertexInputBindingDescription.calloc(1)
                .binding(0)
                .stride(stride)
                .inputRate(VK_VERTEX_INPUT_RATE_VERTEX);
	}
	
	public void createAttributeDescription(){
		
		attributeDescription = VkVertexInputAttributeDescription.calloc(2);
		
		VkVertexInputAttributeDescription.Buffer location0 = VkVertexInputAttributeDescription.calloc(1)
	                .binding(0)
	                .location(0)
	                .format(VK_FORMAT_R32G32_SFLOAT)
	                .offset(0);
		
		VkVertexInputAttributeDescription.Buffer location1 = VkVertexInputAttributeDescription.calloc(1)
	                .binding(0)
	                .location(1)
	                .format(VK_FORMAT_R32G32B32_SFLOAT)
	                .offset(8);
		
		attributeDescription.put(location0);
		attributeDescription.put(location1);
		attributeDescription.flip();
	}

	public VkVertexInputBindingDescription.Buffer getBindingDescription() {
		return bindingDescription;
	}

	public VkVertexInputAttributeDescription.Buffer getAttributeDescription() {
		return attributeDescription;
	}

}
