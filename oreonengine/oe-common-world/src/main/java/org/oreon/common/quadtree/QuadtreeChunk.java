package org.oreon.common.quadtree;

import java.util.Map;

import org.oreon.common.terrain.TerrainConfiguration;
import org.oreon.core.context.EngineContext;
import org.oreon.core.math.Transform;
import org.oreon.core.math.Vec2f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.scenegraph.Node;
import org.oreon.core.scenegraph.NodeComponent;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.RenderList;
import org.oreon.core.scenegraph.Renderable;

public abstract class QuadtreeChunk extends Renderable{
	
	protected Vec3f worldPos;
	protected boolean isleaf;
	protected int lod;
	protected Vec2f location;
	protected Vec2f index;
	protected float gap;
	protected TerrainConfiguration terrainProperties;
	protected QuadtreeCache quadtreeCache;
	
	public QuadtreeChunk(Map<NodeComponentType, NodeComponent> components,
			QuadtreeCache quadtreeCache, Transform worldTransform, 
			Vec2f location, int lod, Vec2f index){
		
		addComponent(NodeComponentType.CONFIGURATION, components.get(NodeComponentType.CONFIGURATION));
		terrainProperties = getComponent(NodeComponentType.CONFIGURATION);
		
		this.quadtreeCache = quadtreeCache; 
		this.isleaf = true;
		this.index = index;
		this.lod = lod;
		this.location = location;
		this.gap = 1f/(terrainProperties.getRootChunkCount() * (float)(Math.pow(2, lod)));
		
		Vec3f localScaling = new Vec3f(gap,0,gap);
		Vec3f localTranslation = new Vec3f(location.getX(),0,location.getY());
		
		getLocalTransform().setScaling(localScaling);
		getLocalTransform().setTranslation(localTranslation);
		setWorldTransform(worldTransform);
		
		computeWorldPos();
	}
	
	public void update()
	{
		for(Node child: getChildren())
			child.update();		
	}
	
	public void render(){
		
		if (isleaf){
			render = true;
		}
		else {
			render = false;
		}
		
		// render only first two lod's for reflection/refraction
		if (EngineContext.getConfig().isRenderReflection() ||
				EngineContext.getConfig().isRenderRefraction()){
			
			if (lod == 0 && isleaf){
				render = true;
			}
			else if(lod == 1 && isleaf){
				render = true;
			}
			else if(lod == 1 && !isleaf){
				render = true;
			}
			else {
				render = false;
			}
		}
		
		if (render){
			super.render();
		}
		
	}
	
	public void record(RenderList renderList){

		if (isleaf){
			render = true;
		}
		else {
			render = false;
		}
		
		// render only first two lod's for reflection/refraction
		if (EngineContext.getConfig().isRenderReflection() ||
				EngineContext.getConfig().isRenderRefraction()){
			
			if (lod == 0 && isleaf){
				render = true;
			}
			else if(lod == 1 && isleaf){
				render = true;
			}
			else if(lod == 1 && !isleaf){
				render = true;
			}
			else {
				render = false;
			}
		}
		
		super.record(renderList);
	}
	
	public void updateQuadtree(Map<String, QuadtreeChunk> leafChunks,
			Map<String, QuadtreeChunk> formerLeafChunks){
		
		updateChildNodes(leafChunks, formerLeafChunks);
		
		for (Node node : getChildren()){
			((QuadtreeChunk) node).updateQuadtree(leafChunks, formerLeafChunks);
		}
	}
	
	private void updateChildNodes(Map<String, QuadtreeChunk> leafChunks,
			Map<String, QuadtreeChunk> formerLeafChunks){
		
		float distance = (EngineContext.getCamera().getPosition().sub(worldPos)).length();
		
		if (distance < terrainProperties.getLod_range()[lod]){
			add4ChildNodes(leafChunks, formerLeafChunks, lod+1);
		}
		else if(distance >= terrainProperties.getLod_range()[lod]){
			removeChildNodes(leafChunks, formerLeafChunks);
		}
	}
	
	private void removeChildNodes(Map<String, QuadtreeChunk> leafChunks,
			Map<String, QuadtreeChunk> formerLeafChunks){
		
		if (!isleaf){
			isleaf = true;
			leafChunks.put(id, this);
			formerLeafChunks.remove(id);
		}
		cacheChildrenTree(leafChunks, formerLeafChunks);
	}
	
	protected void add4ChildNodes(Map<String, QuadtreeChunk> leafChunks,
			Map<String, QuadtreeChunk> formerLeafChunks, int lod){
		
		if (isleaf){
			isleaf = false;
			formerLeafChunks.put(id,this);
			leafChunks.remove(id);
		}
		
		if(getChildren().size() == 0){
			for (int i=0; i<2; i++){
				for (int j=0; j<2; j++){
					
					Vec2f newLocation = location.add(new Vec2f(i*gap/2f,j*gap/2f));
					Vec2f newIndex = new Vec2f(i,j);
					String key = getQuadtreeCacheKey(lod, newLocation, newIndex);
					if (quadtreeCache.contains(key)){
						QuadtreeChunk newChunk = quadtreeCache.getChunk(key);
						leafChunks.put(newChunk.getId(), newChunk);
						addChild(newChunk);
					}
					else{
						QuadtreeChunk newChunk = createChildChunk(getComponents(), quadtreeCache,
								getWorldTransform(), newLocation, lod, newIndex);
						leafChunks.put(newChunk.getId(), newChunk);
						addChild(newChunk);
					}
				}
			}
		}	
	}
	
	public String getQuadtreeCacheKey(){
		
		// LOD|LOC_X|LOC_Y|INDEX_X|INDEX_Y|WORLDPOS
		return String.valueOf(lod) + String.valueOf(location.getX()) + String.valueOf(location.getY())
			+ String.valueOf(index.getX()) + String.valueOf(index.getY()) + worldPos.toString();
	}
	
	public String getQuadtreeCacheKey(int lod, Vec2f location, Vec2f index){
		
		// LOD|LOC_X|LOC_Y|INDEX_X|INDEX_Y
		return String.valueOf(lod) + String.valueOf(location.getX()) + String.valueOf(location.getY())
			+ String.valueOf(index.getX()) + String.valueOf(index.getY()) + worldPos.toString();
	}
	
	public void cacheChildrenTree(Map<String, QuadtreeChunk> leafChunks,
			Map<String, QuadtreeChunk> formerLeafChunks){
		
		// traverse children tree until max LOD
		if (lod < terrainProperties.getLodCount()){
			if(getChildren().size() != 0){
				for (Node child : getChildren()){
					quadtreeCache.addChunk(child);
					leafChunks.remove(child.getId());
					formerLeafChunks.put(child.getId(), (QuadtreeChunk) child);
					((QuadtreeChunk) child).cacheChildrenTree(leafChunks, formerLeafChunks);
				}
				getChildren().clear();
			}
		}
	}
	
	public abstract QuadtreeChunk createChildChunk(Map<NodeComponentType, NodeComponent> components,
			QuadtreeCache quadtreeCache, Transform worldTransform,
			Vec2f location, int levelOfDetail, Vec2f index);
	
	protected abstract void computeWorldPos();
	
	public QuadtreeChunk getQuadtreeParent() {
		return (QuadtreeChunk) getParentNode();
	}
}
