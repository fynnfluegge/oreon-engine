package apps.oreonworlds.assets.plants;

import engine.math.Vec3f;
import engine.scenegraph.Node;
import engine.utils.Constants;
import modules.instancing.InstancingCluster;

public class Tree01 extends Node{

	public Tree01(){
		InstancingCluster trees0 = new Tree01Instanced(20,new Vec3f(1436,0,-1629),
				 									 Constants.Tree0101highPolyModelMatricesBinding,
													 Constants.Tree0101highPolyWorldMatricesBinding);
		
		InstancingCluster trees1 = new Tree01Instanced(20,new Vec3f(868,0,-1173),
				 Constants.Tree0102highPolyModelMatricesBinding,
				 Constants.Tree0102highPolyWorldMatricesBinding);
		InstancingCluster trees2 = new Tree01Instanced(20,new Vec3f(1309,0,-1604),
				 Constants.Tree0103highPolyModelMatricesBinding,
				 Constants.Tree0103highPolyWorldMatricesBinding);
		InstancingCluster trees3 = new Tree01Instanced(20,new Vec3f(892,0,-1037),
				 Constants.Tree0104highPolyModelMatricesBinding,
				 Constants.Tree0104highPolyWorldMatricesBinding);
	
	
		addChild(trees0);
		addChild(trees1);
		addChild(trees2);
		addChild(trees3);
	}
}
