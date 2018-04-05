package org.oreon.core.vk.core.scenegraph;

import org.oreon.core.scenegraph.Component;
import org.oreon.core.vk.core.command.CommandBuffer;
import org.oreon.core.vk.core.pipeline.Pipeline;
import org.oreon.core.vk.wrapper.descriptor.VkDescriptor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VkRenderInfo extends Component{

	private Pipeline pipeline;
	private CommandBuffer commandBuffer;
	private VkDescriptor descriptor;
	
	@Override
	public void render(){
		
	}
	
	@Override
	public void update() {
		
	}
}
