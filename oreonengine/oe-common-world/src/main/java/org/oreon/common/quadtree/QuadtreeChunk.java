package org.oreon.common.quadtree;

import java.util.HashMap;

import org.oreon.common.terrain.TerrainConfiguration;
import org.oreon.core.context.EngineContext;
import org.oreon.core.math.Vec2f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.scenegraph.Node;
import org.oreon.core.scenegraph.NodeComponent;
import org.oreon.core.scenegraph.NodeComponentType;
import org.oreon.core.scenegraph.Renderable;

public class QuadtreeChunk extends Renderable{
	
	private boolean isleaf;
	private TerrainConfiguration config;
	private int lod;
	private Vec2f location;
	private Vec3f worldPos;
	private Vec2f index;
	private float gap;
	
	
	public QuadtreeChunk(HashMap<NodeComponentType, NodeComponent> components, Vec2f location, int lod, Vec2f index){
		
		this.isleaf = true;
		this.index = index;
		this.lod = lod;
		this.location = location;
		this.gap = 1f/(Quadtree.getRootPatches() * (float)(Math.pow(2, lod)));
		
		try {
			addComponent(NodeComponentType.MAIN_RENDERINFO, components.get(NodeComponentType.MAIN_RENDERINFO).clone());
			addComponent(NodeComponentType.WIREFRAME_RENDERINFO, components.get(NodeComponentType.WIREFRAME_RENDERINFO).clone());
			addComponent(NodeComponentType.CONFIGURATION, components.get(NodeComponentType.CONFIGURATION));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		config = getComponent(NodeComponentType.CONFIGURATION);
		
		Vec3f localScaling = new Vec3f(gap,0,gap);
		Vec3f localTranslation = new Vec3f(location.getX(),0,location.getY());
		
		getLocalTransform().setScaling(localScaling);
		getLocalTransform().setTranslation(localTranslation);
		
		getWorldTransform().setLocalScaling(config.getScaleXZ(), config.getScaleY(), config.getScaleXZ());
		getWorldTransform().getLocalTranslation().setX(-config.getScaleXZ()/2f);
		getWorldTransform().getLocalTranslation().setZ(-config.getScaleXZ()/2f);
		getWorldTransform().getLocalTranslation().setY(0);
		
		getWorldTransform().setScaling(getWorldTransform().getLocalScaling());
		getWorldTransform().setTranslation(getWorldTransform().getLocalTranslation());
		
		computeWorldPos();
		updateQuadtree();
	}
	
	public void update()
	{
		getWorldTransform().setScaling(getWorldTransform().getLocalScaling());
		
		for(Node child: getChildren())
			child.update();		
	}
	
	public void render()
	{
		boolean renderChunk = false;
		if (EngineContext.getRenderState().isReflection() || EngineContext.getRenderState().isRefraction()){
			// render only first two lod's for reflection/refraction
			renderChunk = (isleaf && lod == 0) || (!isleaf && lod == 0);// || (!isleaf && lod == 1);
		}
		else{
			renderChunk = isleaf;
		}
		
		if (renderChunk)
		{	
			if (EngineContext.getRenderState().isWireframe()){
				getComponents().get(NodeComponentType.WIREFRAME_RENDERINFO).render();
			}
			else{
				getComponents().get(NodeComponentType.MAIN_RENDERINFO).render();
			}
		}
		
		for(Node child: getChildren())
			child.render();
	}
	
	public void renderShadows()
	{
		if (isleaf){
			if (getComponents().containsKey(NodeComponentType.SHADOW_RENDERINFO)){
				getComponents().get(NodeComponentType.SHADOW_RENDERINFO).render();
			}

		}
		for(Node child: getChildren())
			child.renderShadows();
	}
	
	public void updateQuadtree(){
		
		updateChildNodes();
		
		for (Node node : getChildren()){
			((QuadtreeChunk) node).updateQuadtree();
		}
	}
	
	private void updateChildNodes(){
		
		float distance = (EngineContext.getCamera().getPosition().sub(worldPos)).length();
		
		if (distance < config.getLod_range()[lod]){
			add4ChildNodes(lod+1);
		}
		else if(distance >= config.getLod_range()[lod]){
			removeChildNodes();
		}
	}
	
	private void add4ChildNodes(int lod){
		
		if (isleaf){
			isleaf = false;
		}
		if(getChildren().size() == 0){
			for (int i=0; i<2; i++){
				for (int j=0; j<2; j++){
					addChild(new QuadtreeChunk(getComponents(), location.add(new Vec2f(i*gap/2f,j*gap/2f)), lod, new Vec2f(i,j)));
				}
			}
		}	
	}
	
	private void removeChildNodes(){
		
		if (!isleaf){
			isleaf = true;
		}
		if(getChildren().size() != 0){
			getChildren().clear();
		}
	}
	
	public void computeWorldPos(){
		
		Vec2f loc = location.add(gap/2f).mul(config.getScaleXZ()).sub(config.getScaleXZ()/2f);
		float height = getTerrainHeight(loc.getX(), loc.getY());
		this.worldPos = new Vec3f(loc.getX(),height,loc.getY());
	}
	
	public float getTerrainHeight(float x, float z){
		
		float h = 0;
		
		Vec2f pos = new Vec2f();
		pos.setX(x);
		pos.setY(z);
		pos = pos.add(config.getScaleXZ()/2f);
		pos = pos.div(config.getScaleXZ());
		Vec2f floor = new Vec2f((int) Math.floor(pos.getX()), (int) Math.floor(pos.getY()));
		pos = pos.sub(floor);
		pos = pos.mul(config.getHeightmap().getMetaData().getWidth());
		int x0 = (int) Math.floor(pos.getX());
		int x1 = x0 + 1;
		int z0 = (int) Math.floor(pos.getY());
		int z1 = z0 + 1;
		
		float h0 =  config.getHeightmapDataBuffer().get(config.getHeightmap().getMetaData().getWidth() * z0 + x0);
		float h1 =  config.getHeightmapDataBuffer().get(config.getHeightmap().getMetaData().getWidth() * z0 + x1);
		float h2 =  config.getHeightmapDataBuffer().get(config.getHeightmap().getMetaData().getWidth() * z1 + x0);
		float h3 =  config.getHeightmapDataBuffer().get(config.getHeightmap().getMetaData().getWidth() * z1 + x1);
		
		float percentU = pos.getX() - x0;
        float percentV = pos.getY() - z0;
        
        float dU, dV;
        if (percentU > percentV)
        {   // bottom triangle
            dU = h1 - h0;
            dV = h3 - h1;
        }
        else
        {   // top triangle
            dU = h3 - h2;
            dV = h2 - h0;
        }
        
        h = h0 + (dU * percentU) + (dV * percentV );
        h *= config.getScaleY();
		
		return h;
	}

	public Vec3f getWorldPos() {
		return worldPos;
	}

	public void setWorldPos(Vec3f worldPos) {
		this.worldPos = worldPos;
	}

	public Vec2f getLocation() {
		return location;
	}

	public void setLocation(Vec2f location) {
		this.location = location;
	}

	public TerrainConfiguration getTerrConfig() {
		return config;
	}

	public void setTerrConfig(TerrainConfiguration terrConfig) {
		this.config = terrConfig;
	}
	
	public int getLod() {
		return lod;
	}

	public void setLod(int lod) {
		this.lod = lod;
	}

	public Vec2f getIndex() {
		return index;
	}

	public void setIndex(Vec2f index) {
		this.index = index;
	}
	
	public float getGap(){
		return this.gap;
	}
	
	public QuadtreeChunk getQuadtreeParent() {
		return (QuadtreeChunk) getParentNode();
	}
}
