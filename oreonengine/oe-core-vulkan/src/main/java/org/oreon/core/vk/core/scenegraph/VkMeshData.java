package org.oreon.core.vk.core.scenegraph;

import java.nio.ByteBuffer;

import org.oreon.core.scenegraph.NodeComponent;
import org.oreon.core.vk.core.buffer.VkBuffer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VkMeshData extends NodeComponent{

	private VkBuffer indexBufferObject;
	private VkBuffer vertexBufferObject;
	private ByteBuffer indexBuffer;
	private ByteBuffer vertexBuffer;
	
}
