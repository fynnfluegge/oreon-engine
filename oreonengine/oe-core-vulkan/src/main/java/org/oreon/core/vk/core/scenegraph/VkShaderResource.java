package org.oreon.core.vk.core.scenegraph;

import java.util.ArrayList;
import java.util.List;

import org.oreon.core.scenegraph.NodeComponent;
import org.oreon.core.vk.core.descriptor.DescriptorSet;

import lombok.Getter;

public class VkShaderResource extends NodeComponent{

	@Getter
	private List<DescriptorSet> descriptorSets;
	
	public VkShaderResource() {
	
		descriptorSets = new ArrayList<DescriptorSet>();
	}
	
	public void addDescriporSet(DescriptorSet descriptorSet){
		
		descriptorSets.add(descriptorSet);
	}
}
