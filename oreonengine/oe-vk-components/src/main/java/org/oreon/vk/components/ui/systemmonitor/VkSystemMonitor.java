package org.oreon.vk.components.ui.systemmonitor;

import static org.lwjgl.system.MemoryUtil.memAllocLong;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT;
import static org.lwjgl.vulkan.VK10.VK_ACCESS_MEMORY_READ_BIT;
import static org.lwjgl.vulkan.VK10.VK_ATTACHMENT_LOAD_OP_CLEAR;
import static org.lwjgl.vulkan.VK10.VK_DEPENDENCY_BY_REGION_BIT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R16G16B16A16_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_GENERAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
import static org.lwjgl.vulkan.VK10.VK_SUBPASS_EXTERNAL;

import java.nio.LongBuffer;
import java.util.List;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;
import org.oreon.common.ui.GUI;
import org.oreon.core.context.EngineContext;
import org.oreon.core.vk.framebuffer.FrameBufferColorAttachment;
import org.oreon.core.vk.framebuffer.VkFrameBuffer;
import org.oreon.core.vk.framebuffer.VkFrameBufferObject;
import org.oreon.core.vk.pipeline.RenderPass;
import org.oreon.core.vk.wrapper.image.VkImageBundle;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.sensors.Fan;
import com.profesorfalken.jsensors.model.sensors.Load;
import com.profesorfalken.jsensors.model.sensors.Temperature;

import lombok.Getter;

public class VkSystemMonitor extends GUI implements Runnable{
	
	private Thread thread;
	
	private Components components;
	
	@Getter
	public static GuiFbo fbo;
	
	public void init() {
		
//		LogicalDevice device = VkContext.getDeviceManager().getLogicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE);
//		VkPhysicalDeviceMemoryProperties memoryProperties = 
//				VkContext.getDeviceManager().getPhysicalDevice(DeviceType.MAJOR_GRAPHICS_DEVICE).getMemoryProperties();
//		
//		fbo = new GuiFbo(device.getHandle(), memoryProperties);
//		UIScreen screen0 = new UIScreen();
//		screen0.setElements(new UIElement[1]);
//		screen0.getElements()[0] = new StaticTextPanel("TEST", 0, 0, 100, 100, fbo);
//		getScreens().add(screen0);
		
		thread = new Thread(this);
		thread.start();
	}
	
	public VkSystemMonitor() {
		
	}
	
	public void render(){
		
//		getScreens().get(0).render();
	}
	
	@Override
	public void run(){
		
		while(true){
		components = JSensors.get.components();
		List<Cpu> cpus = components.cpus;
		for (final Cpu cpu : cpus) {
            System.out.println("Found CPU component: " + cpu.name);
            if (cpu.sensors != null) {
              System.out.println("Sensors: ");
              
              //Print temperatures
              List<Temperature> temps = cpu.sensors.temperatures;
              for (final Temperature temp : temps) {
                  System.out.println(temp.name + ": " + temp.value + " C");
              }
  
              //Print fan speed
              List<Fan> fans = cpu.sensors.fans;
              for (final Fan fan : fans) {
                  System.out.println(fan.name + ": " + fan.value + " RPM");
              }
              
              List<Load> loads = cpu.sensors.loads;
              for (final Load load : loads) {
                  System.out.println(load.name + ": " + load.value + " %");
              }
            }
        }
		}
	}
	
	public class GuiFbo extends VkFrameBufferObject{

		public GuiFbo(VkDevice device,
				VkPhysicalDeviceMemoryProperties memoryProperties) {
			
			width = EngineContext.getConfig().getX_ScreenResolution();
			height = EngineContext.getConfig().getY_ScreenResolution();
			
			VkImageBundle colorAttachment = new FrameBufferColorAttachment(device, memoryProperties,
					width, height, VK_FORMAT_R16G16B16A16_SFLOAT, 1);
			
			attachments.put(Attachment.COLOR, colorAttachment);
			
			renderPass = new RenderPass(device);
			renderPass.setAttachment(VK_FORMAT_R16G16B16A16_SFLOAT, 1, VK_IMAGE_LAYOUT_UNDEFINED,
					VK_IMAGE_LAYOUT_GENERAL, VK_ATTACHMENT_LOAD_OP_CLEAR);
			
			renderPass.addColorAttachmentReference(0, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
			renderPass.setSubpassDependency(VK_SUBPASS_EXTERNAL, 0,
					VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT,
					VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
					VK_ACCESS_MEMORY_READ_BIT,
					VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT,
					VK_DEPENDENCY_BY_REGION_BIT);
			renderPass.setSubpassDependency(0, VK_SUBPASS_EXTERNAL,
					VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT,
					VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT,
					VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT,
					VK_ACCESS_MEMORY_READ_BIT,
					VK_DEPENDENCY_BY_REGION_BIT);
			renderPass.createSubpass();
			renderPass.createRenderPass();

			depthAttachment = 0;
			colorAttachmentCount = renderPass.getAttachmentCount()-depthAttachment;
			
			LongBuffer pImageViews = memAllocLong(renderPass.getAttachmentCount());
			pImageViews.put(0, attachments.get(Attachment.COLOR).getImageView().getHandle());

			frameBuffer = new VkFrameBuffer(device, width, height, 1, pImageViews, renderPass.getHandle());
		}

	}
}
