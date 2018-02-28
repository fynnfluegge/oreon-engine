package org.oreon.modules.gl.terrain;

import org.oreon.core.gl.buffers.GLPatchVBO;
import org.oreon.core.math.Vec2f;
import org.oreon.core.scene.Node;
import org.oreon.core.util.MeshGenerator;

public class TerrainQuadtree extends Node{
	
	private static int rootPatches = 8;
		
	public TerrainQuadtree(TerrainConfiguration terrConfig){
		
		GLPatchVBO buffer  = new GLPatchVBO();
		buffer.addData(MeshGenerator.TerrainChunkMesh(),16);
		
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

	public static int getRootPatches() {
		return rootPatches;
	}
}
