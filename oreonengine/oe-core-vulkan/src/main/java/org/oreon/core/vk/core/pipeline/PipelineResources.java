package org.oreon.core.vk.core.pipeline;

import static org.lwjgl.system.MemoryUtil.memAllocLong;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import org.oreon.core.vk.core.descriptor.Descriptor;

import lombok.Getter;

@Getter
public abstract class PipelineResources {

	protected ShaderPipeline shaderPipeline;
	protected VkVertexInput vertexInput;
	protected List<Descriptor> descriptors = new ArrayList<Descriptor>();
	
	public long[] getDescriporSets(){
		
		long[] descriptorSets = new long[descriptors.size()];
		
		for (int i=0; i<descriptors.size(); i++){
			
			descriptorSets[i] = descriptors.get(i).getSet().getHandle();
		}
		
		return descriptorSets;
	}
	
	public LongBuffer getDescriporSetLayouts(){
		
		LongBuffer descriptorSetLayouts = memAllocLong(descriptors.size());
		
		for (Descriptor descriptor : descriptors){
			
			descriptorSetLayouts.put(descriptor.getLayout().getHandle());
		}
		descriptorSetLayouts.flip();
		
		return descriptorSetLayouts;
	}
}
