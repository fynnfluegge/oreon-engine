package org.oreon.core.vk.scenegraph;

import org.oreon.core.scenegraph.NodeComponent;
import org.oreon.core.vk.command.CommandBuffer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VkRenderInfo extends NodeComponent{

	private CommandBuffer commandBuffer;
	
	@Override
	public void shutdown(){
		
		commandBuffer.destroy();
	}

}
