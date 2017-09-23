package org.oreon.core.buffers;

import java.nio.FloatBuffer;

public interface UBO {

	public void updateData(FloatBuffer buffer, int size);
	public void setBinding_point_index(int binding_point_index);
	public void bindBufferBase();
	public void bindBufferBase(int index);
	public void allocate(int size);
}
