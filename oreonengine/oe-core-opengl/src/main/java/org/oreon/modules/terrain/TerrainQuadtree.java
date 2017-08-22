package org.oreon.modules.terrain;

import org.oreon.core.buffers.PatchVBO;
import org.oreon.core.math.Vec2f;
import org.oreon.core.scene.Node;

public class TerrainQuadtree extends Node{
	
	private static int rootPatches = 8;
		
	public TerrainQuadtree(TerrainConfiguration terrConfig){
		
		PatchVBO buffer  = new PatchVBO();
		buffer.addData(generatePatch(),16);
		
		for (int i=0; i<rootPatches; i++){
			for (int j=0; j<rootPatches; j++){
				addChild(new TerrainNode(buffer, terrConfig, new Vec2f(1f * i/(float)rootPatches,1f * j/(float)rootPatches), 0, new Vec2f(i,j)));
			}
		}
		
		getWorldTransform().setLocalScaling(terrConfig.getScaleXZ(), terrConfig.getScaleY(), terrConfig.getScaleXZ());
		getWorldTransform().getLocalTranslation().setX(-terrConfig.getScaleXZ()/2f);
		getWorldTransform().getLocalTranslation().setZ(-terrConfig.getScaleXZ()/2f);
		getWorldTransform().getLocalTranslation().setY(0);
	}	
	
	public void updateQuadtree(){
		for (Node node : getChildren()){
			((TerrainNode) node).updateQuadtree();
		}
	}
	
public Vec2f[] generatePatch(){
		
		// 16 vertices for each patch
		Vec2f[] vertices = new Vec2f[16];
		
		int index = 0;
		
		vertices[index++] = new Vec2f(0,0);
		vertices[index++] = new Vec2f(0.333f,0);
		vertices[index++] = new Vec2f(0.666f,0);
		vertices[index++] = new Vec2f(1,0);
		
		vertices[index++] = new Vec2f(0,0.333f);
		vertices[index++] = new Vec2f(0.333f,0.333f);
		vertices[index++] = new Vec2f(0.666f,0.333f);
		vertices[index++] = new Vec2f(1,0.333f);
		
		vertices[index++] = new Vec2f(0,0.666f);
		vertices[index++] = new Vec2f(0.333f,0.666f);
		vertices[index++] = new Vec2f(0.666f,0.666f);
		vertices[index++] = new Vec2f(1,0.666f);
	
		vertices[index++] = new Vec2f(0,1);
		vertices[index++] = new Vec2f(0.333f,1);
		vertices[index++] = new Vec2f(0.666f,1);
		vertices[index++] = new Vec2f(1,1);
		
		return vertices;
	}

	public static int getRootPatches() {
		return rootPatches;
	}
}
