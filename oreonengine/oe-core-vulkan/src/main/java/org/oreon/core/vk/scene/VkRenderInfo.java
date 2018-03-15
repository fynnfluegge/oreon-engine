package org.oreon.core.vk.scene;

import org.oreon.core.scenegraph.Component;
import org.oreon.core.vk.command.CommandBuffer;
import org.oreon.core.vk.pipeline.Pipeline;

public class VkRenderInfo extends Component{

	private Pipeline pipeline;
	private CommandBuffer commandBuffer;
}
