package simulations.worldEditor;

import engine.core.Constants;
import engine.math.Quaternion;
import engine.renderer.water.WaterSurface;

public class Ocean extends WaterSurface{

	public Ocean() {
		super(128);
		
		setShininess(80);
		setEmission(1);
		
		getTransform().setScaling(Constants.ZFAR,1,Constants.ZFAR);
		getTransform().setTranslation(-Constants.ZFAR/2,200,-Constants.ZFAR/2);
		
		setClipplane(new Quaternion(0,-1,0,getTransform().getTranslation().getY()));
	}

}
