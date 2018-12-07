package org.oreon.core.vk.scenegraph;

import static org.lwjgl.vulkan.VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_ALL_GRAPHICS;
import static org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_COMPUTE_BIT;

import org.lwjgl.vulkan.VkDevice;
import org.oreon.core.math.Vec3f;
import org.oreon.core.scenegraph.Camera;
import org.oreon.core.util.BufferUtil;
import org.oreon.core.vk.context.DeviceManager.DeviceType;
import org.oreon.core.vk.context.VkContext;
import org.oreon.core.vk.descriptor.DescriptorSet;
import org.oreon.core.vk.descriptor.DescriptorSetLayout;
import org.oreon.core.vk.wrapper.buffer.VkUniformBuffer;

import lombok.Getter;

@Getter
public class VkCamera extends Camera{
	
	private VkUniformBuffer uniformBuffer;
	private DescriptorSet descriptorSet;
	private DescriptorSetLayout descriptorSetLayout;

	public VkCamera() {
		
		super(new Vec3f(-160,45,-72), new Vec3f(0.5668308f,-0.028192917f,0.82335174f),
				new Vec3f(0.015936304f,0.9996025f,0.023256794f));
		
		// flip y-axxis for vulkan coordinate system
		getProjectionMatrix().set(1, 1, -getProjectionMatrix().get(1, 1));
	}
	
	@Override
	public void init() {
		
		VkDevice device = VkContext.getDeviceManager().getLogicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE).getHandle();
		
	    uniformBuffer = new VkUniformBuffer(
	    		device, VkContext.getDeviceManager().getPhysicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE)
	    		.getMemoryProperties(), BufferUtil.createByteBuffer(floatBuffer));
	    
	    descriptorSetLayout = new DescriptorSetLayout(device, 1);
	    descriptorSetLayout.addLayoutBinding(0,VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
	    		VK_SHADER_STAGE_ALL_GRAPHICS | VK_SHADER_STAGE_COMPUTE_BIT);
	    descriptorSetLayout.create();
		
	    descriptorSet = new DescriptorSet(device,
	    		VkContext.getDeviceManager().getLogicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE)
	    		.getDescriptorPool(Thread.currentThread().getId()).getHandle(),
	    		descriptorSetLayout.getHandlePointer());
	    descriptorSet.updateDescriptorBuffer(uniformBuffer.getHandle(), bufferSize, 0, 0,
	    		VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
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
