package org.oreon.core.vk.core.context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.oreon.core.vk.core.descriptor.DescriptorKeys.DescriptorPoolType;
import org.oreon.core.vk.core.descriptor.DescriptorKeys.DescriptorSetKey;
import org.oreon.core.vk.core.descriptor.DescriptorPool;
import org.oreon.core.vk.wrapper.descriptor.VkDescriptor;

public class VkEnvironment {
	
	private HashMap<DescriptorSetKey, VkDescriptor> descriptorsSets; 
	private HashMap<DescriptorPoolType, DescriptorPool> descriptorPools;
	
	public VkEnvironment() {
		
		descriptorsSets = new HashMap<DescriptorSetKey, VkDescriptor>();
		descriptorPools = new HashMap<DescriptorPoolType, DescriptorPool>();
	}
	
	public void addDescriptorSet(DescriptorSetKey key, VkDescriptor descriptorSet){
		
		descriptorsSets.put(key, descriptorSet);
	}
	
	public VkDescriptor getDescriptorSet(DescriptorSetKey key){
		
		return descriptorsSets.get(key);
	}
	
	public void addDescriptorPool(DescriptorPoolType type, DescriptorPool descriptorPool){
		
		descriptorPools.put(type, descriptorPool);
	}
	
	public DescriptorPool getDescriptorPool(DescriptorPoolType type){
		
		return descriptorPools.get(type);
	}
	
	public void shutdown(){
		
		Iterator<Entry<DescriptorSetKey, VkDescriptor>> setIterator = descriptorsSets.entrySet().iterator();
		while (setIterator.hasNext()) {
			setIterator.next().getValue().destroy();
		}
		
		Iterator<Entry<DescriptorPoolType, DescriptorPool>> poolIterator = descriptorPools.entrySet().iterator();
		while (poolIterator.hasNext()) {
			poolIterator.next().getValue().destroy();
		}
	}

}
