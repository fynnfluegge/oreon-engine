package org.oreon.core.vk.platform;

import org.oreon.core.platform.Camera;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.vk.context.VkContext;
import org.oreon.core.vk.wrapper.buffer.VkUniformBuffer;
import org.oreon.core.vk.wrapper.descriptor.CameraDescriptor;

import lombok.Getter;

public class VkCamera extends Camera{
	
	private VkUniformBuffer uniformBuffer;
	@Getter
	private CameraDescriptor descriptor;

	public VkCamera() {
		  
		super();
		
		// flip y-axxis for vulkan coordinate system
		getProjectionMatrix().set(1, 1, -getProjectionMatrix().get(1, 1));
	}
	
	@Override
	public void init() {
		
	    uniformBuffer = new VkUniformBuffer(VkContext.getLogicalDevice().getHandle(),
	    		VkContext.getPhysicalDevice().getMemoryProperties(),
	    		BufferUtil.createByteBuffer(floatBuffer));
	    
	    descriptor = new CameraDescriptor(VkContext.getLogicalDevice().getHandle(),
	    		uniformBuffer.getHandle(), bufferSize);
	}
	
	@Override
	public void update(){
		
		super.update();
		
		uniformBuffer.updateData(BufferUtil.createByteBuffer(floatBuffer));
	}
	
	public void shutdown(){

		uniformBuffer.destroy();
	}

}
