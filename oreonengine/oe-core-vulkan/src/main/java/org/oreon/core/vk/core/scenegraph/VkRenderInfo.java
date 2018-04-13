package org.oreon.core.vk.core.scenegraph;

import org.lwjgl.vulkan.VkQueue;
import org.oreon.core.scenegraph.Component;
import org.oreon.core.vk.core.command.CommandBuffer;
import org.oreon.core.vk.core.command.SubmitInfo;
import org.oreon.core.vk.core.pipeline.VkPipeline;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VkRenderInfo extends Component{

	private VkPipeline pipeline;
	private CommandBuffer commandBuffer;
	private SubmitInfo submitInfo;
	private VkQueue queue;
	
	@Override
	public void render(){
		
		submitInfo.submit(queue);
	}

}
