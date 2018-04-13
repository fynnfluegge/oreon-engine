package org.oreon.core.vk.core.context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.oreon.core.vk.core.descriptor.DescriptorKeys.DescriptorPoolType;
import org.oreon.core.vk.core.descriptor.DescriptorKeys.DescriptorSetKey;
import org.oreon.core.vk.core.descriptor.DescriptorPool;
import org.oreon.core.vk.core.descriptor.Descriptor;

public class VkEnvironment {
	
	private HashMap<DescriptorSetKey, Descriptor> descriptors; 
	private HashMap<DescriptorPoolType, DescriptorPool> descriptorPools;
	
	public VkEnvironment() {
		
		descriptors = new HashMap<DescriptorSetKey, Descriptor>();
		descriptorPools = new HashMap<DescriptorPoolType, DescriptorPool>();
	}
	
	public void addDescriptor(DescriptorSetKey key, Descriptor descriptor){
		
		descriptors.put(key, descriptor);
	}
	
	public Descriptor getDescriptor(DescriptorSetKey key){
		
		return descriptors.get(key);
	}
	
	public void addDescriptorPool(DescriptorPoolType type, DescriptorPool descriptorPool){
		
		descriptorPools.put(type, descriptorPool);
	}
	
	public DescriptorPool getDescriptorPool(DescriptorPoolType type){
		
		return descriptorPools.get(type);
	}
	
	public void shutdown(){
		
		Iterator<Entry<DescriptorSetKey, Descriptor>> setIterator = descriptors.entrySet().iterator();
		while (setIterator.hasNext()) {
			setIterator.next().getValue().destroy();
		}
		
		Iterator<Entry<DescriptorPoolType, DescriptorPool>> poolIterator = descriptorPools.entrySet().iterator();
		while (poolIterator.hasNext()) {
			poolIterator.next().getValue().destroy();
		}
	}

}
