package org.oreon.core.vk.core.scenegraph;

import org.oreon.core.scenegraph.Component;
import org.oreon.core.vk.core.command.CommandBuffer;
import org.oreon.core.vk.core.pipeline.Pipeline;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VkRenderInfo extends Component{

	private Pipeline pipeline;
	private CommandBuffer commandBuffer;
}
