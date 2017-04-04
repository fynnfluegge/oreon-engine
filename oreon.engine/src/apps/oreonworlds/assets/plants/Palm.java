package apps.oreonworlds.assets.plants;

import engine.math.Vec3f;
import engine.scenegraph.Node;
import engine.utils.Constants;

public class Palm extends Node{
	
	public Palm(){
		
		PalmInstanced palms0 = new PalmInstanced(40,new Vec3f(822,0,-7),
													 Constants.Palm001highPolyModelMatricesBinding,
													 Constants.Palm001highPolyWorldMatricesBinding);
		
		PalmInstanced palms1 = new PalmInstanced(30,new Vec3f(1128,0,-121),
				 Constants.Palm002highPolyModelMatricesBinding,
				 Constants.Palm002highPolyWorldMatricesBinding);
		
		PalmInstanced palms2 = new PalmInstanced(30,new Vec3f(-706,0,1872),
				 Constants.Palm003highPolyModelMatricesBinding,
				 Constants.Palm003highPolyWorldMatricesBinding);
		PalmInstanced palms3 = new PalmInstanced(30,new Vec3f(-1409,0,1627),
				 Constants.Palm004highPolyModelMatricesBinding,
				 Constants.Palm004highPolyWorldMatricesBinding);
		PalmInstanced palms4 = new PalmInstanced(30,new Vec3f(-1206,0,1764),
				 Constants.Palm005highPolyModelMatricesBinding,
				 Constants.Palm005highPolyWorldMatricesBinding);

		addChild(palms0);
		addChild(palms1);
		addChild(palms2);
		addChild(palms3);
		addChild(palms4);
	}

}
