package modules.terrain;

import engine.math.Vec2f;
import engine.scenegraph.Node;

public class TerrainQuadtree extends Node{
	
	private static int rootPatches = 8;
		
	public TerrainQuadtree(TerrainConfiguration terrConfig){
		
		for (int i=0; i<rootPatches; i++){
			for (int j=0; j<rootPatches; j++){
				addChild(new TerrainNode(terrConfig, new Vec2f(1f * i/(float)rootPatches,1f * j/(float)rootPatches), 0, new Vec2f(i,j)));
			}
		}
		
		getTransform().setLocalScaling(terrConfig.getScaleXZ(), terrConfig.getScaleY(), terrConfig.getScaleXZ());
		getTransform().getLocalTranslation().setX(-terrConfig.getScaleXZ()/2f);
		getTransform().getLocalTranslation().setZ(-terrConfig.getScaleXZ()/2f);
		getTransform().getLocalTranslation().setY(0);
	}	
	
	public void updateQuadtree(){
		for (Node node : getChildren()){
			((TerrainNode) node).updateQuadtree();
		}
	}

	public static int getRootPatches() {
		return rootPatches;
	}
}
