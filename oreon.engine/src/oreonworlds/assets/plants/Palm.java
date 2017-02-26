package oreonworlds.assets.plants;

import engine.math.Vec3f;
import engine.scenegraph.Node;
import engine.utils.Constants;

public class Palm extends Node{
	
	public Palm(){
		
		PalmInstanced palms0 = new PalmInstanced(new Vec3f(980,0,-1170),
													 Constants.Palm001highPolyModelMatricesBinding,
													 Constants.Palm001highPolyWorldMatricesBinding,
													 Constants.Palm001BillboardModelMatricesBinding,
													 Constants.Palm001BillboardWorldMatricesBinding);
		PalmInstanced palms1 = new PalmInstanced(new Vec3f(1196,0,-450),
													 Constants.Palm002highPolyModelMatricesBinding,
													 Constants.Palm002highPolyWorldMatricesBinding,
													 Constants.Palm002BillboardModelMatricesBinding,
													 Constants.Palm002BillboardWorldMatricesBinding);

		addChild(palms0);
		addChild(palms1);
	}

}
