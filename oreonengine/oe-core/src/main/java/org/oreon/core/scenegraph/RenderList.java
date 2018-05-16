package org.oreon.core.scenegraph;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class RenderList {

	@Getter
	private LinkedHashMap<String, Renderable> objectList;
	@Getter @Setter
	private boolean changed;
	
	public RenderList() {
	
		objectList = new LinkedHashMap<String, Renderable>();
	}
	
	public boolean contains(String id){
		
		return objectList.containsKey(id);
	}
	
	public void add(Renderable object){
		objectList.put(object.getId(), object);
	}
	
	public void remove(Renderable object){
		objectList.remove(object.getId());
	}
	
	public Set<String> getKeySet(){
		
		return objectList.keySet();
	}
	
	public Set<Map.Entry<String, Renderable>> getEntrySet(){
		
		return objectList.entrySet();
	}
	
	public void sortFrontToBack(){
		
		// TODO
	}
	
	public void sortBackToFront(){
		
		// TODO
	}
}
