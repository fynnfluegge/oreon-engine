package apps.oreonworlds.assets.plants;

import engine.math.Vec3f;
import engine.scenegraph.Node;
import engine.utils.Constants;
import modules.instancing.InstancingCluster;

public class Grass01 extends Node{

	public Grass01(){
		InstancingCluster grass0 = new Grass01Instanced(500,new Vec3f(1020,0,-1191),
				 									 Constants.Grass0101highPolyModelMatricesBinding,
													 Constants.Grass0101highPolyWorldMatricesBinding);
		InstancingCluster grass1 = new Grass01Instanced(500,new Vec3f(1064,0,-1344),
				 Constants.Grass0102highPolyModelMatricesBinding,
				 Constants.Grass0102highPolyWorldMatricesBinding);
		InstancingCluster grass2 = new Grass01Instanced(500,new Vec3f(1129,0,-1478),
				 Constants.Grass0103highPolyModelMatricesBinding,
				 Constants.Grass0103highPolyWorldMatricesBinding);
//		InstancedObject grass3 = new Grass01Instanced(500,new Vec3f(1214,0,-1684),
//				 Constants.Grass0104highPolyModelMatricesBinding,
//				 Constants.Grass0104highPolyWorldMatricesBinding);
		InstancingCluster grass4 = new Grass01Instanced(500,new Vec3f(1086,0,-1100),
				 Constants.Grass0105highPolyModelMatricesBinding,
				 Constants.Grass0105highPolyWorldMatricesBinding);
	
		addChild(grass0);
		addChild(grass1);
		addChild(grass2);
//		addChild(grass3);
		addChild(grass4);
	}
}
