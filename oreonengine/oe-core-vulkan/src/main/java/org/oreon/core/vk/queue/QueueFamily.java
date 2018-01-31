package org.oreon.core.vk.queue;

public class QueueFamily {
	
	private int index;
	private int count;
	private int flags;
	private int presentFlag;
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getFlags() {
		return flags;
	}
	public void setFlags(int flags) {
		this.flags = flags;
	}
	public int getPresentFlag() {
		return presentFlag;
	}
	public void setPresentFlag(int presentFlag) {
		this.presentFlag = presentFlag;
	}

}
