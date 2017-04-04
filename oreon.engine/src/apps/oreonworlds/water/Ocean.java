package apps.oreonworlds.water;

import engine.math.Quaternion;
import engine.utils.Constants;
import modules.water.Water;

public class Ocean extends Water{

	public Ocean() {
		super(256,512);
		
		getTransform().setScaling(Constants.ZFAR,1,Constants.ZFAR);
		getTransform().setTranslation(-Constants.ZFAR/2,-100,-Constants.ZFAR/2);
		
		setClipplane(new Quaternion(0,-1,0,-100));

		this.loadSettingsFile("./res/samples/Ocean/waterSettings.txt");
	}
}
