package apps.oreonworlds.assets.plants;

import engine.math.Vec3f;
import engine.scenegraph.Node;
import engine.utils.Constants;

public class Tree01 extends Node{

	public Tree01(){
		Tree01Instanced trees0 = new Tree01Instanced(new Vec3f(1060,0,-830),
				 									 Constants.Tree0101highPolyModelMatricesBinding,
													 Constants.Tree0101highPolyWorldMatricesBinding);
	
		addChild(trees0);
	}
}
