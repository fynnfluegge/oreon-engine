package org.oreon.core.vk.core.platform;

import static org.lwjgl.system.MemoryUtil.memAlloc;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.oreon.core.platform.Camera;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.vk.core.buffers.VkUniformBuffer;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.descriptor.DescriptorKeys.DescriptorSetKey;
import org.oreon.core.vk.wrapper.descriptor.CameraDescriptor;

public class VkCamera extends Camera{
	
	private VkUniformBuffer uniformBuffer;
	private CameraDescriptor descriptor;

	public VkCamera() {
		  
		super();
		
		// flip y-axxis for vulkan coordinate system
		getProjectionMatrix().set(1, 1, -getProjectionMatrix().get(1, 1));
	}
	
	@Override
	public void init() {
		
		ByteBuffer cameraBuffer = memAlloc(4 * 16);
		FloatBuffer cameraMatrix = cameraBuffer.asFloatBuffer();
		cameraMatrix.put(BufferUtil.createFlippedBuffer(getViewProjectionMatrix()));
		
	    uniformBuffer = new VkUniformBuffer(VkContext.getLogicalDevice().getHandle(),
	    									VkContext.getPhysicalDevice().getMemoryProperties(),
	    									BufferUtil.createByteBuffer(getViewProjectionMatrix()));
	    
	    descriptor = new CameraDescriptor(VkContext.getLogicalDevice().getHandle(),
	    									 uniformBuffer.getHandle());
	    VkContext.getEnvironment().addDescriptorSet(DescriptorSetKey.CAMERA,descriptor);
	}
	
	@Override
	public void update(){
		
		super.update();
		
		uniformBuffer.updateData(VkContext.getLogicalDevice().getHandle(),
								 BufferUtil.createByteBuffer(getViewProjectionMatrix()));
	}
	
	public void shutdown(){

		uniformBuffer.destroy();
	}

}
