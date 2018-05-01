package org.oreon.vk.components.atmosphere;

import org.oreon.core.scenegraph.Renderable;
import org.oreon.core.util.Constants;

public class Skydome extends Renderable{
	
	public Skydome() {
		
		getWorldTransform().setLocalScaling(Constants.ZFAR*0.5f, Constants.ZFAR*0.5f, Constants.ZFAR*0.5f);
	}

}
