package org.oreon.demo.gl.oreonworlds.water;

import org.oreon.core.math.Quaternion;
import org.oreon.core.util.Constants;
import org.oreon.modules.gl.water.Water;

public class Ocean extends Water{

	public Ocean() {
		super(64,256);
		
		getWorldTransform().setScaling(Constants.ZFAR,1,Constants.ZFAR);
		getWorldTransform().setTranslation(-Constants.ZFAR/2,-80,-Constants.ZFAR/2);
		
		setClip_offset(2);
		setClipplane(new Quaternion(0,-1,0,getWorldTransform().getTranslation().getY() + getClip_offset()));

		this.loadSettingsFile("src/main/resources/oreonworlds/water/waterSettings.txt");
	}
}
