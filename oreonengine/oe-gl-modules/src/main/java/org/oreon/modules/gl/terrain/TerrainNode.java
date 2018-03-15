package org.oreon.modules.gl.terrain;

import org.oreon.core.gl.buffers.GLPatchVBO;
import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.scene.GLRenderInfo;
import org.oreon.core.math.Vec2f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.scenegraph.ComponentType;
import org.oreon.core.scenegraph.Node;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.system.CoreSystem;

public class TerrainNode extends Renderable{
	
	private GLPatchVBO buffer;
	private boolean isleaf;
	private TerrainConfiguration terrConfig;
	private int lod;
	private Vec2f location;
	private Vec3f worldPos;
	private Vec2f index;
	private float gap;
	
	
	public TerrainNode(GLPatchVBO buffer, TerrainConfiguration terrConfig, Vec2f location, int lod, Vec2f index){
		
		this.buffer = buffer;
		this.isleaf = true;
		this.index = index;
		this.lod = lod;
		this.location = location;
		this.terrConfig = terrConfig;
		this.gap = 1f/(TerrainQuadtree.getRootPatches() * (float)(Math.pow(2, lod)));

		GLRenderInfo renderInfo = new GLRenderInfo(terrConfig.getShader(),
												   new Default(),
												   buffer);
		
		GLRenderInfo wireframeRenderInfo = new GLRenderInfo(terrConfig.getGridShader(),
														    new Default(),
														    buffer);
		
		addComponent(ComponentType.MAIN_RENDERINFO, renderInfo);
		addComponent(ComponentType.WIREFRAME_RENDERINFO, wireframeRenderInfo);
		
		Vec3f localScaling = new Vec3f(gap,0,gap);
		Vec3f localTranslation = new Vec3f(location.getX(),0,location.getY());
		
		getLocalTransform().setScaling(localScaling);
		getLocalTransform().setTranslation(localTranslation);
		
		getWorldTransform().setLocalScaling(terrConfig.getScaleXZ(), terrConfig.getScaleY(), terrConfig.getScaleXZ());
		getWorldTransform().getLocalTranslation().setX(-terrConfig.getScaleXZ()/2f);
		getWorldTransform().getLocalTranslation().setZ(-terrConfig.getScaleXZ()/2f);
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
		if (isleaf)
		{	
			if (CoreSystem.getInstance().getRenderEngine().isWireframe()){
				getComponents().get(ComponentType.WIREFRAME_RENDERINFO).render();
			}
			else{
				getComponents().get(ComponentType.MAIN_RENDERINFO).render();
			}
		}
		for(Node child: getChildren())
			child.render();
	}
	
	public void renderShadows()
	{
		if (isleaf){
			if (getComponents().containsKey(ComponentType.SHADOW_RENDERINFO)){
				getComponents().get(ComponentType.SHADOW_RENDERINFO).render();
			}

		}
		for(Node child: getChildren())
			child.renderShadows();
	}
	
	public void updateQuadtree(){
		
		if (CoreSystem.getInstance().getScenegraph().getCamera().getPosition().getY() > (terrConfig.getScaleY())){
			worldPos.setY(terrConfig.getScaleY());
		}
		else worldPos.setY(CoreSystem.getInstance().getScenegraph().getCamera().getPosition().getY());

		updateChildNodes();
		
		for (Node node : getChildren()){
			((TerrainNode) node).updateQuadtree();
		}
	}
	
	private void updateChildNodes(){
		
		float distance = (CoreSystem.getInstance().getScenegraph().getCamera().getPosition().sub(worldPos)).length();
		
		if (distance < terrConfig.getLod_range()[lod]){
			add4ChildNodes(lod+1);
		}
		else if(distance >= terrConfig.getLod_range()[lod]){
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
					addChild(new TerrainNode(buffer, terrConfig, location.add(new Vec2f(i*gap/2f,j*gap/2f)), lod, new Vec2f(i,j)));
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
		
		Vec2f loc = location.add(gap/2f).mul(terrConfig.getScaleXZ()).sub(terrConfig.getScaleXZ()/2f);
		float height = CoreSystem.getInstance().getScenegraph().getTerrain().getTerrainHeight(loc.getX(), loc.getY());
		this.worldPos = new Vec3f(loc.getX(),height,loc.getY());
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
		return terrConfig;
	}

	public void setTerrConfig(TerrainConfiguration terrConfig) {
		this.terrConfig = terrConfig;
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
	
	public TerrainNode getQuadtreeParent() {
		return (TerrainNode) getParent();
	}
}
