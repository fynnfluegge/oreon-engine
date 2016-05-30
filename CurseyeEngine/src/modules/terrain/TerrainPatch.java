package modules.terrain;

import engine.buffers.PatchVAO;
import engine.configs.AlphaBlending;
import engine.core.Camera;
import engine.main.RenderingEngine;
import engine.math.Vec2f;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;
import engine.scenegraph.components.PatchRenderer;
import engine.scenegraph.components.Renderer;

public class TerrainPatch extends GameObject{
	
	private TerrainConfiguration terrConfig;
	private int lod;
	private Vec2f location;
	private Vec3f worldPos;
	private Vec2f index;
	private float gap;
	
	
	public TerrainPatch(TerrainConfiguration terrConfig, Vec2f location, int lod, Vec2f index){
		
		this.index = index;
		this.lod = lod;
		this.location = location;
		this.terrConfig = terrConfig;
		this.gap = 1f/(10f * (float)(Math.pow(2, lod)));
		PatchVAO meshBuffer = new PatchVAO();
		meshBuffer.addData(generatePatch(),16);
		PatchRenderer renderer = new PatchRenderer(meshBuffer, terrConfig.getTessellationShader(), new engine.configs.Default());
		addComponent("Renderer", renderer);
		computeWorldPos();
		updateQuadtree();
	}
	
	public void update()
	{	
			getTransform().setLocalScaling(terrConfig.getScaleXZ(), terrConfig.getScaleY(), terrConfig.getScaleXZ());
			getTransform().getLocalTranslation().setX(-terrConfig.getScaleXZ()/2f);
			getTransform().getLocalTranslation().setZ(-terrConfig.getScaleXZ()/2f);
			
			getTransform().setScaling(getTransform().getLocalScaling());
			getTransform().setTranslation(getTransform().getLocalTranslation());
			
			if (RenderingEngine.isGrid())
			{
				if (getComponents().containsKey("Renderer"))
				((Renderer) getComponents().get("Renderer")).setShader(terrConfig.getGridShader());
			}
			else if (!RenderingEngine.isGrid())
			{
				if (getComponents().containsKey("Renderer"))
				((Renderer) getComponents().get("Renderer")).setShader(terrConfig.getTessellationShader());
			}
			
			if(TerrainQuadtree.isCameraMoved()){
				updateQuadtree();
			}
			
			for(GameObject child: getChildren())
				child.update();
	}
	
	public void updateQuadtree(){
		
		float distance;
		
		if (Camera.getInstance().getPosition().getY() > (terrConfig.getScaleY())){
			worldPos.setY(terrConfig.getScaleY());
		}
		else worldPos.setY(Camera.getInstance().getPosition().getY());

		distance = (Camera.getInstance().getPosition().sub(worldPos)).length();
		
		if (this.lod == 0){
			if (distance < terrConfig.getLod1_range()){
				add4ChildNodes(lod+1);
			}
			else if(distance >= terrConfig.getLod1_range()){
				removeChildNodes();
			}
		}
		if (this.lod == 1){
			if (distance < terrConfig.getLod2_range()){
				add4ChildNodes(lod+1);
			}
			else if(distance >= terrConfig.getLod2_range()){
				removeChildNodes();
			}
		}
		if (this.lod == 2){
			if (distance < terrConfig.getLod3_range()){
				add4ChildNodes(lod+1);
			}
			else if(distance >= terrConfig.getLod3_range()){
				removeChildNodes();
			}
		}	
		if (this.lod == 3){
			if (distance < terrConfig.getLod4_range()){
				add4ChildNodes(lod+1);
			}
			else if(distance >= terrConfig.getLod4_range()){
				removeChildNodes();
			}
		}
		if (this.lod == 4){
			if (distance < terrConfig.getLod5_range()){
				add4ChildNodes(lod+1);
			}
			else if(distance >= terrConfig.getLod5_range()){
				removeChildNodes();
			}
		}
		if (this.lod == 5){
			if (distance < terrConfig.getLod6_range()){
				add4ChildNodes(lod+1);
			}
			else if(distance >= terrConfig.getLod6_range()){
				removeChildNodes();
			}
		} 
		if (this.lod == 6){
			if (distance < terrConfig.getLod7_range()){
				add4ChildNodes(lod+1);
			}
			else if(distance >= terrConfig.getLod7_range()){
				removeChildNodes();
			}
		} 
		if (this.lod == 7){
			if (distance < terrConfig.getLod8_range()){
				add4ChildNodes(lod+1);
			}
			else if(distance >= terrConfig.getLod8_range()){
				removeChildNodes();
			}
		} 
	}
	
	public void add4ChildNodes(int lod){
		
		if (getComponents().containsKey("Renderer")){
			((PatchRenderer) getComponent("Renderer")).getVao().delete();
			getComponents().remove("Renderer");
		}
		if(getChildren().size() == 0){
			for (int i=0; i<2; i++){
				for (int j=0; j<2; j++){
					addChild(new TerrainPatch(terrConfig, location.add(new Vec2f(i*gap/2f,j*gap/2f)), lod, new Vec2f(i,j)));
				}
			}
		}	
	}
	
	public void removeChildNodes(){
		
		if (!getComponents().containsKey("Renderer")){
			PatchVAO meshBuffer = new PatchVAO();
			meshBuffer.addData(generatePatch(),16);
			PatchRenderer renderer;
			if (RenderingEngine.isGrid())
				renderer = new PatchRenderer(meshBuffer, terrConfig.getGridShader(), new AlphaBlending(0.0f));
			else
				renderer = new PatchRenderer(meshBuffer, terrConfig.getTessellationShader(), new AlphaBlending(0.0f));

			addComponent("Renderer", renderer);
		}
		if(getChildren().size() != 0){
			
			for(GameObject child: getChildren()){
				((PatchRenderer) child.getComponent("Renderer")).getVao().delete();
			}	
			getChildren().clear();
		}
	}
	
	public void computeWorldPos(){
		
		Vec2f loc = location.add(gap/2f).mul(terrConfig.getScaleXZ()).sub(terrConfig.getScaleXZ()/2f);
		this.worldPos = new Vec3f(loc.getX(),terrConfig.getScaleY(),loc.getY());
	}
	
	public Vec2f[] generatePatch(){
		
		// 16 vertices for each patch
		Vec2f[] vertices = new Vec2f[16];
		
		int index = 0;
		
		vertices[index++] = new Vec2f(location.getX(),location.getY());
		vertices[index++] = new Vec2f(location.getX()+gap*0.3333f,location.getY());
		vertices[index++] = new Vec2f(location.getX()+gap*0.6666f,location.getY());
		vertices[index++] = new Vec2f(location.getX()+gap,location.getY());
		
		vertices[index++] = new Vec2f(location.getX(),location.getY()+gap*0.3333f);
		vertices[index++] = new Vec2f(location.getX()+gap*0.3333f,location.getY()+gap*0.3333f);
		vertices[index++] = new Vec2f(location.getX()+gap*0.6666f,location.getY()+gap*0.3333f);
		vertices[index++] = new Vec2f(location.getX()+gap,location.getY()+gap*0.3333f);
		
		vertices[index++] = new Vec2f(location.getX(),location.getY()+gap*0.6666f);
		vertices[index++] = new Vec2f(location.getX()+gap*0.3333f,location.getY()+gap*0.6666f);
		vertices[index++] = new Vec2f(location.getX()+gap*0.6666f,location.getY()+gap*0.6666f);
		vertices[index++] = new Vec2f(location.getX()+gap,location.getY()+gap*0.6666f);
	
		vertices[index++] = new Vec2f(location.getX(),location.getY()+gap);
		vertices[index++] = new Vec2f(location.getX()+gap*0.3333f,location.getY()+gap);
		vertices[index++] = new Vec2f(location.getX()+gap*0.6666f,location.getY()+gap);
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
}
