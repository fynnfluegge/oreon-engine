package apps.oreonworlds.assets.plants;

import engine.math.Vec3f;
import engine.scenegraph.Node;
import engine.utils.Constants;

public class Palm extends Node{
	
	public Palm(){
		
		PalmInstanced palms0 = new PalmInstanced(new Vec3f(980,0,-1170),
													 Constants.Palm001highPolyModelMatricesBinding,
													 Constants.Palm001highPolyWorldMatricesBinding);
		PalmInstanced palms1 = new PalmInstanced(new Vec3f(1196,0,-450),
													 Constants.Palm002highPolyModelMatricesBinding,
													 Constants.Palm002highPolyWorldMatricesBinding);

		addChild(palms0);
		addChild(palms1);
	}

}
