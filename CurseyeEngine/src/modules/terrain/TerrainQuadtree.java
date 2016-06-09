package modules.terrain;

import cdk.tools.terrainEditor.TerrainEditorInterface;
import engine.math.Vec2f;
import engine.scenegraph.GameObject;

public class TerrainQuadtree extends GameObject{
	
	private static int rootPatches = 10;
		
	public TerrainQuadtree(TerrainConfiguration terrConfig){
		init(terrConfig);
	}	
	
	public void init(TerrainConfiguration terrConfig){
		
		for (int i=0; i<rootPatches; i++){
			for (int j=0; j<rootPatches; j++){
				
				addChild(new TerrainPatch(terrConfig, new Vec2f(i/(float)rootPatches,j/(float)rootPatches), 0, new Vec2f(i,j)));
			}
		}
	}
	
	public void update()
	{
		TerrainEditorInterface.updateLoDPatchesChart();
		
		super.update();
	}

	public static int getRootPatches() {
		return rootPatches;
	}
}
