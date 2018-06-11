package org.oreon.core.vk.platform;

import org.oreon.core.math.Vec3f;
import org.oreon.core.platform.Camera;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.vk.context.DeviceManager.DeviceType;
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
		
		setPosition(new Vec3f(0,40,0));
		
		// flip y-axxis for vulkan coordinate system
		getProjectionMatrix().set(1, 1, -getProjectionMatrix().get(1, 1));
	}
	
	@Override
	public void init() {
		
	    uniformBuffer = new VkUniformBuffer(
	    		VkContext.getDeviceManager().getLogicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE).getHandle(),
	    		VkContext.getDeviceManager().getPhysicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE).getMemoryProperties(),
	    		BufferUtil.createByteBuffer(floatBuffer));
	    
	    descriptor = new CameraDescriptor(
	    		VkContext.getDeviceManager().getLogicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE).getHandle(),
	    		VkContext.getDeviceManager().getLogicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE)
	    			.getDescriptorPool(Thread.currentThread().getId()).getHandle(),
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
