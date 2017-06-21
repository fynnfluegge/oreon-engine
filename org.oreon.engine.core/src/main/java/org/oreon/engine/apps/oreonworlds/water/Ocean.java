package org.oreon.engine.apps.oreonworlds.water;

import org.oreon.engine.engine.math.Quaternion;
import org.oreon.engine.engine.utils.Constants;
import org.oreon.engine.modules.water.Water;

public class Ocean extends Water{

	public Ocean() {
		super(64,256);
		
		getTransform().setScaling(Constants.ZFAR,1,Constants.ZFAR);
		getTransform().setTranslation(-Constants.ZFAR/2,-200,-Constants.ZFAR/2);
		
		setClip_offset(2);
		setClipplane(new Quaternion(0,-1,0,getTransform().getTranslation().getY() + getClip_offset()));

		this.loadSettingsFile("./res/samples/Ocean/waterSettings.txt");
	}
}
