package apps.oreonworlds.assets.rocks;

import engine.math.Vec3f;
import engine.scenegraph.Node;
import engine.utils.Constants;
import modules.instancing.InstancingCluster;

public class Rock01 extends Node{

	public Rock01(){
		InstancingCluster rocks0 = new Rock01Instanced(10, new Vec3f(1601,0,-1836),
				 									 Constants.Rock0101highPolyModelMatricesBinding,
													 Constants.Rock0101highPolyWorldMatricesBinding);
		
		InstancingCluster rocks1 = new Rock01Instanced(10, new Vec3f(1936,0,-1941),
													 Constants.Rock0102highPolyModelMatricesBinding,
													 Constants.Rock0102highPolyWorldMatricesBinding);
		InstancingCluster rocks2 = new Rock01Instanced(20, new Vec3f(1918,0,64),
				 Constants.Rock0103highPolyModelMatricesBinding,
				 Constants.Rock0103highPolyWorldMatricesBinding);
		InstancingCluster rocks3 = new Rock01Instanced(20, new Vec3f(800,0,-5),
				 Constants.Rock0104highPolyModelMatricesBinding,
				 Constants.Rock0104highPolyWorldMatricesBinding);
		InstancingCluster rocks4 = new Rock01Instanced(10, new Vec3f(1126,0,-149),
				 Constants.Rock0105highPolyModelMatricesBinding,
				 Constants.Rock0105highPolyWorldMatricesBinding);
//		InstancedObject rocks5 = new Rock01Instanced(10, new Vec3f(-1686,0,1254),
//				 Constants.Rock0106highPolyModelMatricesBinding,
//				 Constants.Rock0106highPolyWorldMatricesBinding);
	
		addChild(rocks0);
		addChild(rocks1);
		addChild(rocks2);
		addChild(rocks3);
		addChild(rocks4);
//		addChild(rocks5);
	}
}
