package apps.oreonworlds.water;

import engine.math.Quaternion;
import engine.utils.Constants;
import modules.water.Water;

public class Ocean extends Water{

	public Ocean() {
		super(128,256);
		
		getTransform().setScaling(Constants.ZFAR,1,Constants.ZFAR);
		getTransform().setTranslation(-Constants.ZFAR/2,-200,-Constants.ZFAR/2);
		
		setClip_offset(2);
		setClipplane(new Quaternion(0,-1,0,getTransform().getTranslation().getY() + getClip_offset()));

		this.loadSettingsFile("./res/samples/Ocean/waterSettings.txt");
	}
}
