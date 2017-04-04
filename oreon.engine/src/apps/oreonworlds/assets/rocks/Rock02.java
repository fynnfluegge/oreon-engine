package apps.oreonworlds.assets.rocks;

import engine.math.Vec3f;
import engine.scenegraph.Node;
import engine.utils.Constants;
import modules.instancing.InstancingCluster;

public class Rock02 extends Node{

	public Rock02(){
		InstancingCluster rocks0 = new Rock02Instanced(40, new Vec3f(1060,0,-830),
				 									 Constants.Rock0201highPolyModelMatricesBinding,
													 Constants.Rock0201highPolyWorldMatricesBinding);
	
		addChild(rocks0);
	}
}
