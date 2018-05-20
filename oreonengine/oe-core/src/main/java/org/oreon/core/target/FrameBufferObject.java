package org.oreon.core.target;

import lombok.Getter;

@Getter
public abstract class FrameBufferObject {

	protected int height; 
	protected int width;
	protected boolean depthAttachment;
	protected int attachmentCount;
}
