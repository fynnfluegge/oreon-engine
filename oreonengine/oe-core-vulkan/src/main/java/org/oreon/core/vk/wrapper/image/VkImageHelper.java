package org.oreon.core.vk.wrapper.image;

import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_UNORM;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_GENERAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_STORAGE_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_TRANSFER_BIT;
import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;

import java.nio.ByteBuffer;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.lwjgl.vulkan.VkQueue;
import org.oreon.core.image.ImageMetaData;
import org.oreon.core.vk.image.VkImage;
import org.oreon.core.vk.image.VkImageLoader;
import org.oreon.core.vk.wrapper.buffer.StagingBuffer;
import org.oreon.core.vk.wrapper.command.ImageCopyCmdBuffer;
import org.oreon.core.vk.wrapper.command.ImageMemoryBarrierCmdBuffer;

public class VkImageHelper {
	
	public static VkImage createStorageImageFromFile(VkDevice device,
			VkPhysicalDeviceMemoryProperties memoryProperties,
			long commandPool, VkQueue queue, String file){
		
		return createImage(device, memoryProperties, commandPool,
				queue, file, VK_IMAGE_USAGE_STORAGE_BIT,
				VK_IMAGE_LAYOUT_GENERAL,
				VK_PIPELINE_STAGE_COMPUTE_SHADER_BIT);
	}
	
	public static VkImage createSampledImageFromFile(VkDevice device,
			VkPhysicalDeviceMemoryProperties memoryProperties,
			long commandPool, VkQueue queue, String file){
		
		return createImage(device, memoryProperties, commandPool,
				queue, file, VK_IMAGE_USAGE_SAMPLED_BIT,
				VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL,
				VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT);
	}
	
	private static VkImage createImage(VkDevice device,
			VkPhysicalDeviceMemoryProperties memoryProperties, long commandPool, 
			VkQueue queue, String file, int usage, int layout, int dstStageMask){
		
		ImageMetaData metaData = VkImageLoader.getImageMetaData(file);
		ByteBuffer imageBuffer = VkImageLoader.decodeImage(file);
		
		StagingBuffer stagingBuffer = new StagingBuffer(device, memoryProperties, imageBuffer);
		
		VkImage image = new Image2DLocal(device,
				memoryProperties, metaData.getWidth(), metaData.getHeight(),
				VK_FORMAT_R8G8B8A8_UNORM, VK_IMAGE_USAGE_TRANSFER_DST_BIT | usage);
	    
	    // transition layout barrier
	    ImageMemoryBarrierCmdBuffer imageMemoryBarrierLayout0 = new ImageMemoryBarrierCmdBuffer(device, commandPool);
	    imageMemoryBarrierLayout0.record(image.getHandle(),
	    		VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
	    		VK_PIPELINE_STAGE_TRANSFER_BIT);
	    imageMemoryBarrierLayout0.submit(queue);
	    
	    // copy buffer to image
	    ImageCopyCmdBuffer imageCopyCmd = new ImageCopyCmdBuffer(device, commandPool);
	    imageCopyCmd.record(stagingBuffer.getHandle(), image.getHandle(), metaData);
	    imageCopyCmd.submit(queue);
	    
	    // transition layout barrier
	    ImageMemoryBarrierCmdBuffer imageMemoryBarrierLayout1 = new ImageMemoryBarrierCmdBuffer(device, commandPool);
		imageMemoryBarrierLayout1.record(image.getHandle(),
				VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, layout, dstStageMask);
		imageMemoryBarrierLayout1.submit(queue);
		
		vkQueueWaitIdle(queue);
		
		imageMemoryBarrierLayout0.destroy();
		imageMemoryBarrierLayout1.destroy();
		imageCopyCmd.destroy();
		stagingBuffer.destroy();
		
		return image;
	}
}
