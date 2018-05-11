package org.oreon.core.vk.context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.oreon.core.vk.descriptor.DescriptorPool;

public class DescriptorPoolManager {
	
	private HashMap<String, DescriptorPool> descriptorPools;

	public DescriptorPoolManager() {
		
		descriptorPools = new HashMap<String, DescriptorPool>();
	}
	
	public void addDescriptorPool(String key, DescriptorPool descriptorPool){
		
		descriptorPools.put(key, descriptorPool);
	}
	
	public DescriptorPool getDescriptorPool(String key){
		
		return descriptorPools.get(key);
	}
	
	public void shutdown(){
		
		Iterator<Entry<String, DescriptorPool>> poolIterator = descriptorPools.entrySet().iterator();
		while (poolIterator.hasNext()) {
			poolIterator.next().getValue().destroy();
		}
	}
}
