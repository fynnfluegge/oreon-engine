package apps.oreonworlds.assets.plants;

import engine.math.Vec3f;
import engine.scenegraph.Node;
import engine.utils.Constants;
import modules.instancing.InstancingCluster;

public class Plant01 extends Node{

	public Plant01(){
		InstancingCluster plants0 = new Plant01Instanced(100,new Vec3f(1020,0,-1191),
				 									 Constants.Plant0101highPolyModelMatricesBinding,
													 Constants.Plant0101highPolyWorldMatricesBinding);
		
		InstancingCluster plants1 = new Plant01Instanced(100,new Vec3f(1057,0,-1383),
				 Constants.Plant0102highPolyModelMatricesBinding,
				 Constants.Plant0102highPolyWorldMatricesBinding);
//		InstancedObject plants2 = new Plant01Instanced(100,new Vec3f(1199,0,-1652),
//				 Constants.Plant0103highPolyModelMatricesBinding,
//				 Constants.Plant0103highPolyWorldMatricesBinding);
	
		addChild(plants0);
		addChild(plants1);
//		addChild(plants2);
	}

}
