package org.oreon.core.vk.core.platform;

import org.oreon.core.platform.Camera;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.core.descriptor.DescriptorKeys.DescriptorSetKey;
import org.oreon.core.vk.wrapper.buffer.VkUniformBuffer;
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
		
	    uniformBuffer = new VkUniformBuffer(VkContext.getLogicalDevice().getHandle(),
	    									VkContext.getPhysicalDevice().getMemoryProperties(),
	    									BufferUtil.createByteBuffer(getViewProjectionMatrix()));
	    
	    descriptor = new CameraDescriptor(VkContext.getLogicalDevice().getHandle(),
	    									 uniformBuffer.getHandle());
	    VkContext.getEnvironment().addDescriptor(DescriptorSetKey.CAMERA, descriptor);
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
