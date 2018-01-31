package org.oreon.core.vk.pipeline;

import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;

public class Swapchain {
	
	private long swapChainHandle;
	private VkSurfaceCapabilitiesKHR capabilities;
	private VkSurfaceFormatKHR format;
	private int presenMode;
	private VkExtent2D extent;
	
	public Swapchain() {
		// TODO Auto-generated constructor stub
	}

	public VkSurfaceCapabilitiesKHR getCapabilities() {
		return capabilities;
	}

	public VkSurfaceFormatKHR getFormat() {
		return format;
	}

	public int getPresenMode() {
		return presenMode;
	}

	public VkExtent2D getExtent() {
		return extent;
	}

	public long getSwapChainHandle() {
		return swapChainHandle;
	}

}
