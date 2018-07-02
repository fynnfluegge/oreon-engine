package org.oreon.core.vk.scenegraph;

import java.nio.ByteBuffer;

import org.oreon.core.scenegraph.NodeComponent;
import org.oreon.core.vk.memory.VkBuffer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class VkMeshData extends NodeComponent{

	private VkBuffer vertexBufferObject;
	private ByteBuffer vertexBuffer;
	private int vertexCount;
	
	private VkBuffer indexBufferObject;
	private ByteBuffer indexBuffer;
	private int indexCount;
	
}
