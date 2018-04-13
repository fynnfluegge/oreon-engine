package org.oreon.core.vk.wrapper;

import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT;
import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;

import java.nio.ByteBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkQueue;
import org.oreon.core.vk.core.buffer.VkBuffer;
import org.oreon.core.vk.core.image.VkImage;
import org.oreon.core.vk.core.image.VkImageLoader;
import org.oreon.core.vk.wrapper.buffer.DeviceLocalBuffer;
import org.oreon.core.vk.wrapper.buffer.StagingBuffer;
import org.oreon.core.vk.wrapper.command.BufferCopyCmd;
import org.oreon.core.vk.wrapper.command.ImageCopyCmd;
import org.oreon.core.vk.wrapper.command.ImageLayoutTransitionCmd;

public class VkMemoryHelper {

	public static VkBuffer createDeviceLocalBuffer(VkDevice device, 
											   	   VkPhysicalDeviceMemoryProperties memoryProperties,
											   	   long commandPool,
											   	   VkQueue queue,
											   	   ByteBuffer dataBuffer,
											   	   int usage){
		
		StagingBuffer stagingBuffer = new StagingBuffer(device, memoryProperties, dataBuffer);
		DeviceLocalBuffer deviceLocalBuffer = new DeviceLocalBuffer(device, memoryProperties,
														dataBuffer.limit(), usage);
	    
	    BufferCopyCmd bufferCopyCommand = new BufferCopyCmd(device, commandPool);
	    bufferCopyCommand.record(stagingBuffer.getHandle(),
	    					     deviceLocalBuffer.getHandle(), 0, 0, dataBuffer.limit());
	    bufferCopyCommand.submit(queue);
	    
	    vkQueueWaitIdle(queue);
	    
	    bufferCopyCommand.destroy();
	    stagingBuffer.destroy();
	    
	    return deviceLocalBuffer;
	}
	
	public static VkImage createImageFromFile(VkDevice device, 
		   	   						  VkPhysicalDeviceMemoryProperties memoryProperties,
		   	   						  long commandPool,
		   	   						  VkQueue queue,
		   	   						  String file){
		
		ByteBuffer imageBuffer = VkImageLoader.loadImage(file);
	    
		StagingBuffer stagingBuffer = new StagingBuffer(device, memoryProperties, imageBuffer);
		
		VkImage image = new VkImage(device, 512, 512, 1, VK_FORMAT_R8G8B8A8_UNORM,
	    			 VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT);
	    image.allocate(memoryProperties,
	    			   VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
	    image.bindImageMemory();
	    
	    // transition layout
	    ImageLayoutTransitionCmd imageLayoutTransitionCmd1 = new ImageLayoutTransitionCmd(device, commandPool);
	    imageLayoutTransitionCmd1.record(image.getHandle(),
	    						VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL);
	    imageLayoutTransitionCmd1.submit(queue);
	    
	    // copy buffer to image
	    ImageCopyCmd imageCopyCmd = new ImageCopyCmd(device, commandPool);
	    imageCopyCmd.record(stagingBuffer.getHandle(), image.getHandle());
	    imageCopyCmd.submit(queue);
	    
	    // transition layout
	    ImageLayoutTransitionCmd imageLayoutTransitionCmd2 = new ImageLayoutTransitionCmd(device, commandPool);
		imageLayoutTransitionCmd2.record(image.getHandle(),
				VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
		imageLayoutTransitionCmd2.submit(queue);
		
		vkQueueWaitIdle(queue);
		
		imageLayoutTransitionCmd1.destroy();
		imageLayoutTransitionCmd2.destroy();
		imageCopyCmd.destroy();
		stagingBuffer.destroy();
		
		return image;
	}
}
