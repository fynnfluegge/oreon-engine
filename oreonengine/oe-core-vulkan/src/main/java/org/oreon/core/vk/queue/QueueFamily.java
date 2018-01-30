package org.oreon.core.vk.queue;

public class QueueFamily {

	private int index;
	private int queueCount;
	private int flags;
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getQueueCount() {
		return queueCount;
	}
	public void setQueueCount(int queueCount) {
		this.queueCount = queueCount;
	}
	public int getFlags() {
		return flags;
	}
	public void setFlags(int flags) {
		this.flags = flags;
	}
}
