package org.oreon.core.vk.pipeline;

import org.lwjgl.vulkan.VkQueue;

public class Queue {
	
	private int familyIndex;
	private int count;
	private VkQueue vkQueueHandle;

	public int getFamilyIndex() {
		return familyIndex;
	}

	public void setFamilyIndex(int familyIndex) {
		this.familyIndex = familyIndex;
	}

	public VkQueue getVkQueueHandle() {
		return vkQueueHandle;
	}

	public void setVkQueueHandle(VkQueue vkQueueHandle) {
		this.vkQueueHandle = vkQueueHandle;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
