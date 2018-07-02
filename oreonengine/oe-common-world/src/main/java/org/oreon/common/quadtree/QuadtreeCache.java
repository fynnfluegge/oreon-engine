package org.oreon.common.quadtree;

import java.util.HashMap;

import org.oreon.core.scenegraph.Node;

import lombok.Getter;

public class QuadtreeCache {

	@Getter
	private HashMap<String, QuadtreeChunk> chunks;
	
	public QuadtreeCache() {
		
		chunks = new HashMap<String, QuadtreeChunk>();
	}
	
	public boolean contains(String key){
		
		return chunks.containsKey(key);
	}
	
	public void addChunk(Node chunk){
		
		chunks.put(((QuadtreeChunk) chunk).getQuadtreeCacheKey(), (QuadtreeChunk) chunk);
	}
	
	public void addChunk(QuadtreeChunk chunk){
	
		chunks.put(chunk.getQuadtreeCacheKey(), chunk);
	}
	
	public QuadtreeChunk getChunk(String key){
		
		return chunks.get(key);
	}
	
	public void removeChunk(String key){
		
		chunks.remove(key);
	}
	
	public QuadtreeChunk getAndRemoveChunk(String key){
		
		QuadtreeChunk chunk = chunks.get(key);
		chunks.remove(key);
		return chunk;
	}
}
