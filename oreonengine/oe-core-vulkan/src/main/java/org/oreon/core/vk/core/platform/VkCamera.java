package org.oreon.core.vk.core.platform;

import static org.lwjgl.system.MemoryUtil.memAlloc;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.oreon.core.platform.Camera;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.vk.core.buffers.VkUniformBuffer;
import org.oreon.core.vk.core.context.VkContext;
import org.oreon.core.vk.wrapper.descriptor.CameraDescriptor;

import lombok.Getter;

public class VkCamera extends Camera{
	
	@Getter
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
	    									cameraBuffer);
	    
	    descriptor = new CameraDescriptor(uniformBuffer.getHandle());
	    VkContext.registerObject(descriptor);
	}
	
	@Override
	public void update(){
		
		super.update();
		
		ByteBuffer cameraBuffer = memAlloc(4 * 16);
		FloatBuffer cameraMatrix = cameraBuffer.asFloatBuffer();
		cameraMatrix.put(BufferUtil.createFlippedBuffer(getViewProjectionMatrix()));
		uniformBuffer.updateData(VkContext.getLogicalDevice().getHandle(), cameraBuffer);
	}

}
