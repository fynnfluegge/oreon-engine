package modules.terrain;

import static org.lwjgl.opengl.GL11.glViewport;

import engine.buffers.PatchVAO;
import engine.core.Camera;
import engine.core.Window;
import engine.core.RenderingEngine;
import engine.math.Vec2f;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.Node;
import engine.scenegraph.components.RenderInfo;
import engine.scenegraph.components.Renderer;
import engine.utils.Constants;

public class TerrainNode extends GameObject{
	
	private boolean isleaf;
	private TerrainConfiguration terrConfig;
	private int lod;
	private Vec2f location;
	private Vec3f worldPos;
	private Vec2f index;
	private float gap;
	
	
	public TerrainNode(TerrainConfiguration terrConfig, Vec2f location, int lod, Vec2f index){

		this.isleaf = true;
		this.index = index;
		this.lod = lod;
		this.location = location;
		this.terrConfig = terrConfig;
		this.gap = 1f/(TerrainQuadtree.getRootPatches() * (float)(Math.pow(2, lod)));
		PatchVAO meshBuffer = new PatchVAO();
		meshBuffer.addData(generatePatch(),16);
		
		setRenderInfo(new RenderInfo(new engine.configs.Default(),terrConfig.getShader()));

		if (RenderingEngine.isGrid())
			getRenderInfo().setShader(terrConfig.getGridShader());
		else if (!RenderingEngine.isGrid())
			getRenderInfo().setShader(terrConfig.getShader());
		
		Renderer renderer = new Renderer(getRenderInfo().getShader(), meshBuffer);
		addComponent("Renderer", renderer);
		
		getTransform().setLocalScaling(terrConfig.getScaleXZ(), terrConfig.getScaleY(), terrConfig.getScaleXZ());
		getTransform().getLocalTranslation().setX(-terrConfig.getScaleXZ()/2f);
		getTransform().getLocalTranslation().setZ(-terrConfig.getScaleXZ()/2f);
		getTransform().getLocalTranslation().setY(0);
		
		getTransform().setScaling(getTransform().getLocalScaling());
		getTransform().setTranslation(getTransform().getLocalTranslation());
		
		computeWorldPos();
		updateQuadtree();
	}
	
	public void update()
	{
			if (RenderingEngine.isGrid())
				getRenderInfo().setShader(terrConfig.getGridShader());
			else if (!RenderingEngine.isGrid())
				getRenderInfo().setShader(terrConfig.getShader());
			
			getComponents().get("Renderer").setShader(getRenderInfo().getShader());
					
			getTransform().setScaling(getTransform().getLocalScaling());
			
			for(Node child: getChildren())
				child.update();		
	}
	
	public void render()
	{
		if (isleaf)
		{	
			getRenderInfo().getConfig().enable();
			getComponents().get("Renderer").render();
			getRenderInfo().getConfig().disable();
		}
		for(Node child: getChildren())
			child.render();
	}
	
	public void renderShadows()
	{
		if (getRenderInfo().isShadowCaster() && isleaf){
			getComponents().get("Renderer").setShader(getRenderInfo().getShadowShader());
			getRenderInfo().getConfig().enable();
			glViewport(0,0,Constants.PSSM_SHADOWMAP_RESOLUTION,Constants.PSSM_SHADOWMAP_RESOLUTION);
			
			getComponents().get("Renderer").render();
			
			glViewport(0,0,Window.getInstance().getWidth(), Window.getInstance().getHeight());
			getRenderInfo().getConfig().disable();
			getComponents().get("Renderer").setShader(getRenderInfo().getShader());
		}
		for(Node child: getChildren())
			child.renderShadows();
	}
	
	public void updateQuadtree(){
		
		if (Camera.getInstance().getPosition().getY() > (terrConfig.getScaleY())){
			worldPos.setY(terrConfig.getScaleY());
		}
		else worldPos.setY(Camera.getInstance().getPosition().getY());

		updateChildNodes();
		
		for (Node node : getChildren()){
			((TerrainNode) node).updateQuadtree();
		}
	}
	
	private void updateChildNodes(){
		
		float distance = (Camera.getInstance().getPosition().sub(worldPos)).length();
		
		switch (lod){
		case 0: if (distance < terrConfig.getLod_range()[0]){
					add4ChildNodes(lod+1);
				}
				else if(distance >= terrConfig.getLod_range()[0]){
					removeChildNodes();
				}
				break;
		case 1: if (distance < terrConfig.getLod_range()[1]){
					add4ChildNodes(lod+1);
				}
				else if(distance >= terrConfig.getLod_range()[1]){
					removeChildNodes();
				}
				break;
		case 2: if (distance < terrConfig.getLod_range()[2]){
					add4ChildNodes(lod+1);
				}
				else if(distance >= terrConfig.getLod_range()[2]){
					removeChildNodes();
				}
				break;
		case 3: if (distance < terrConfig.getLod_range()[3]){
					add4ChildNodes(lod+1);
				}
				else if(distance >= terrConfig.getLod_range()[3]){
					removeChildNodes();
				}
				break;
		case 4: if (distance < terrConfig.getLod_range()[4]){
					add4ChildNodes(lod+1);
				}
				else if(distance >= terrConfig.getLod_range()[4]){
					removeChildNodes();
				}
				break;
		case 5: if (distance < terrConfig.getLod_range()[5]){
					add4ChildNodes(lod+1);
				}
				else if(distance >= terrConfig.getLod_range()[5]){
					removeChildNodes();
				}
				break;
		case 6: if (distance < terrConfig.getLod_range()[6]){
					add4ChildNodes(lod+1);
				}
				else if(distance >= terrConfig.getLod_range()[6]){
					removeChildNodes();
				}
				break;
		case 7: if (distance < terrConfig.getLod_range()[7]){
					add4ChildNodes(lod+1);
				}
				else if(distance >= terrConfig.getLod_range()[7]){
					removeChildNodes();
				}
				break;
		}
	}
	
	private void add4ChildNodes(int lod){
		
		if (isleaf){
			isleaf = false;
			((Renderer) getComponent("Renderer")).getVao().delete();
		}
		if(getChildren().size() == 0){
			for (int i=0; i<2; i++){
				for (int j=0; j<2; j++){
					addChild(new TerrainNode(terrConfig, location.add(new Vec2f(i*gap/2f,j*gap/2f)), lod, new Vec2f(i,j)));
				}
			}
		}	
	}
	
	private void removeChildNodes(){
		
		if (!isleaf){
			isleaf = true;
			PatchVAO meshBuffer = new PatchVAO();
			meshBuffer.addData(generatePatch(),16);
			Renderer renderer;
			if (RenderingEngine.isGrid())
				renderer = new Renderer(terrConfig.getGridShader(), meshBuffer);
			else
				renderer = new Renderer(terrConfig.getShader(), meshBuffer);
			addComponent("Renderer", renderer);
		}
		if(getChildren().size() != 0){
			
			for(Node child: getChildren()){
				((Renderer) ((GameObject) child).getComponent("Renderer")).getVao().delete();
			}	
			getChildren().clear();
		}
}
	
	public void computeWorldPos(){
		
		Vec2f loc = location.add(gap/2f).mul(terrConfig.getScaleXZ()).sub(terrConfig.getScaleXZ()/2f);
		float height = Terrain.getInstance().getTerrainHeight(loc.getX(), loc.getY());
		this.worldPos = new Vec3f(loc.getX(),height,loc.getY());
	}
	
	public Vec2f[] generatePatch(){
		
		// 16 vertices for each patch
		Vec2f[] vertices = new Vec2f[16];
		
		int index = 0;
		
		vertices[index++] = new Vec2f(location.getX(),location.getY());
		vertices[index++] = new Vec2f(location.getX()+gap*0.333f,location.getY());
		vertices[index++] = new Vec2f(location.getX()+gap*0.666f,location.getY());
		vertices[index++] = new Vec2f(location.getX()+gap,location.getY());
		
		vertices[index++] = new Vec2f(location.getX(),location.getY()+gap*0.333f);
		vertices[index++] = new Vec2f(location.getX()+gap*0.333f,location.getY()+gap*0.333f);
		vertices[index++] = new Vec2f(location.getX()+gap*0.666f,location.getY()+gap*0.333f);
		vertices[index++] = new Vec2f(location.getX()+gap,location.getY()+gap*0.333f);
		
		vertices[index++] = new Vec2f(location.getX(),location.getY()+gap*0.666f);
		vertices[index++] = new Vec2f(location.getX()+gap*0.333f,location.getY()+gap*0.666f);
		vertices[index++] = new Vec2f(location.getX()+gap*0.666f,location.getY()+gap*0.666f);
		vertices[index++] = new Vec2f(location.getX()+gap,location.getY()+gap*0.666f);
	
		vertices[index++] = new Vec2f(location.getX(),location.getY()+gap);
		vertices[index++] = new Vec2f(location.getX()+gap*0.333f,location.getY()+gap);
		vertices[index++] = new Vec2f(location.getX()+gap*0.666f,location.getY()+gap);
		vertices[index++] = new Vec2f(location.getX()+gap,location.getY()+gap);
		
		return vertices;
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
