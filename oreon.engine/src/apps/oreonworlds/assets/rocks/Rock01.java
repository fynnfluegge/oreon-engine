package apps.oreonworlds.assets.rocks;

import engine.math.Vec3f;
import engine.scenegraph.Node;
import engine.utils.Constants;

public class Rock01 extends Node{

	public Rock01(){
		Rock01Instanced rocks0 = new Rock01Instanced(new Vec3f(1060,0,-830),
				 									 Constants.Rock0101highPolyModelMatricesBinding,
													 Constants.Rock0101highPolyWorldMatricesBinding);
	
		addChild(rocks0);
	}
}
