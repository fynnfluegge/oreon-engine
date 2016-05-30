package modules.terrain;

import engine.core.Camera;
import engine.math.Vec2f;
import engine.math.Vec3f;
import engine.scenegraph.GameObject;

public class TerrainQuadtree extends GameObject{
	
	private static boolean cameraMoved = false;
	private static int rootPatches = 10;
	private Vec3f preCamPos;
		
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
		if (preCamPos != Camera.getInstance().getPosition())
			cameraMoved = true;
		super.update();
		
		preCamPos = Camera.getInstance().getPosition();
		cameraMoved = false;
	}

	public static boolean isCameraMoved() {
		return cameraMoved;
	}

	public static int getRootPatches() {
		return rootPatches;
	}
}
