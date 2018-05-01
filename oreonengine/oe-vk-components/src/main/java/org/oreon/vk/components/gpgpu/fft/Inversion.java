package org.oreon.vk.components.gpgpu.fft;

import org.oreon.core.vk.core.command.CommandBuffer;
import org.oreon.core.vk.core.descriptor.Descriptor;
import org.oreon.core.vk.core.pipeline.ShaderModule;
import org.oreon.core.vk.core.pipeline.VkPipeline;

public class Inversion {

	private Descriptor descriptor;
	private VkPipeline pipeline;
	private CommandBuffer commandBuffer;
	private ShaderModule computeShader;
	
	private class InversionDescriptor extends Descriptor{
		
		public InversionDescriptor() {
			// TODO Auto-generated constructor stub
		}
	}
	
	public Inversion() {
		// TODO Auto-generated constructor stub
	}
}
