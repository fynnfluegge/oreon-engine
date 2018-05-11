package org.oreon.core.vk.scenegraph;

import java.nio.ByteBuffer;

import org.oreon.core.scenegraph.NodeComponent;
import org.oreon.core.vk.buffer.VkBuffer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VkMeshData extends NodeComponent{

	private VkBuffer vertexBufferObject;
	private ByteBuffer vertexBuffer;
	
	private VkBuffer indexBufferObject;
	private ByteBuffer indexBuffer;
	
}
