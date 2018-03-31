package org.oreon.core.vk.wrapper.descriptor;

import java.nio.LongBuffer;

import lombok.Getter;

@Getter
public abstract class VkDescriptor {

	protected long set;
	protected LongBuffer layout;
	protected long pool;
	
}
