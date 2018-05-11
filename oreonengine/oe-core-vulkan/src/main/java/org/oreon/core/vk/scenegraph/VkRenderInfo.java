package org.oreon.core.vk.scenegraph;

import org.lwjgl.vulkan.VkQueue;
import org.oreon.core.scenegraph.NodeComponent;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.command.SubmitInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VkRenderInfo extends NodeComponent{

	private CommandBuffer commandBuffer;
	private SubmitInfo submitInfo;
	private VkQueue queue;
	
	@Override
	public void render(){
		
		submitInfo.submit(queue);
	}
	
	@Override
	public void shutdown(){
		
		commandBuffer.destroy();
	}

}
