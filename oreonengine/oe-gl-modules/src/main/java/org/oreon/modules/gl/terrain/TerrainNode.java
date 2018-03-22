package org.oreon.modules.gl.terrain;

import java.util.HashMap;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.buffers.GLPatchVBO;
import org.oreon.core.gl.parameter.Default;
import org.oreon.core.gl.scenegraph.GLRenderInfo;
import org.oreon.core.gl.shaders.GLShader;
import org.oreon.core.math.Vec2f;
import org.oreon.core.math.Vec3f;
import org.oreon.core.scenegraph.Component;
import org.oreon.core.scenegraph.ComponentType;
import org.oreon.core.scenegraph.Node;
import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.MeshGenerator;

public class TerrainNode extends Renderable{
	
	private boolean isleaf;
	private TerrainConfiguration terrConfig;
	private int lod;
	private Vec2f location;
	private Vec3f worldPos;
	private Vec2f index;
	private float gap;
	private GLShader shader;
	private GLShader wireframeShader;
	private GLPatchVBO buffer;
	
	
	public TerrainNode(GLPatchVBO buffer, GLShader shader, GLShader wireframeShader, HashMap<ComponentType, Component> components, TerrainConfiguration terrConfig, Vec2f location, int lod, Vec2f index){
		
		this.buffer = buffer;
		this.shader = shader;
		this.wireframeShader = wireframeShader;
		this.isleaf = true;
		this.index = index;
		this.lod = lod;
		this.location = location;
		this.terrConfig = terrConfig;
		this.gap = 1f/(TerrainQuadtree.getRootPatches() * (float)(Math.pow(2, lod)));
		
		GLRenderInfo renderInfo = new GLRenderInfo(shader,
				   new Default(),
				   buffer);

		GLRenderInfo wireframeRenderInfo = new GLRenderInfo(wireframeShader,
						    new Default(),
						    buffer);
		
		addComponent(ComponentType.MAIN_RENDERINFO, renderInfo);
		addComponent(ComponentType.WIREFRAME_RENDERINFO, wireframeRenderInfo);
		
//		try {
//			addComponent(ComponentType.MAIN_RENDERINFO, components.get(ComponentType.MAIN_RENDERINFO).clone());
//			addComponent(ComponentType.WIREFRAME_RENDERINFO, components.get(ComponentType.WIREFRAME_RENDERINFO).clone());
//		} catch (CloneNotSupportedException e) {
//			e.printStackTrace();
//		}
		
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
		boolean renderChunk = false;
		if (EngineContext.getRenderConfig().isReflection() || EngineContext.getRenderConfig().isRefraction()){
			// render only first lod for reflection/refraction
			renderChunk = (isleaf && lod == 0) || (!isleaf && lod == 0);
		}
		else{
			renderChunk = isleaf;
		}
		
		if (renderChunk)
		{	
			if (EngineContext.getRenderConfig().isWireframe()){
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
					addChild(new TerrainNode(buffer,shader, wireframeShader, getComponents(), terrConfig, location.add(new Vec2f(i*gap/2f,j*gap/2f)), lod, new Vec2f(i,j)));
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
		float height = getTerrainHeight(loc.getX(), loc.getY());
		this.worldPos = new Vec3f(loc.getX(),height,loc.getY());
	}
	
	public float getTerrainHeight(float x, float z){
		
		float h = 0;
		
		Vec2f pos = new Vec2f();
		pos.setX(x);
		pos.setY(z);
		pos = pos.add(terrConfig.getScaleXZ()/2f);
		pos = pos.div(terrConfig.getScaleXZ());
		Vec2f floor = new Vec2f((int) Math.floor(pos.getX()), (int) Math.floor(pos.getY()));
		pos = pos.sub(floor);
		pos = pos.mul(terrConfig.getHeightmap().getWidth());
		int x0 = (int) Math.floor(pos.getX());
		int x1 = x0 + 1;
		int z0 = (int) Math.floor(pos.getY());
		int z1 = z0 + 1;
		
		float h0 =  terrConfig.getHeightmapDataBuffer().get(terrConfig.getHeightmap().getWidth() * z0 + x0);
		float h1 =  terrConfig.getHeightmapDataBuffer().get(terrConfig.getHeightmap().getWidth() * z0 + x1);
		float h2 =  terrConfig.getHeightmapDataBuffer().get(terrConfig.getHeightmap().getWidth() * z1 + x0);
		float h3 =  terrConfig.getHeightmapDataBuffer().get(terrConfig.getHeightmap().getWidth() * z1 + x1);
		
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
        h *= terrConfig.getScaleY();
		
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
