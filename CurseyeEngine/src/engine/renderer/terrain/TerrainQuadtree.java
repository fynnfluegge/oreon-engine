package engine.renderer.terrain;

import engine.core.Camera;
import engine.gameObject.GameObject;
import engine.math.Vec2f;
import engine.math.Vec3f;

public class TerrainQuadtree extends GameObject{
	
	private static boolean cameraMoved = false;
	private Vec3f preCamPos;
		
	public TerrainQuadtree(TerrainConfiguration terrConfig){
		init(terrConfig);
	}	
	
	public void init(TerrainConfiguration terrConfig){
		
		for (int i=0; i<10; i++){
			for (int j=0; j<10; j++){
				
				addChild(new TerrainPatch(terrConfig, new Vec2f(i/10f,j/10f), 0, new Vec2f(i,j)));
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
}
