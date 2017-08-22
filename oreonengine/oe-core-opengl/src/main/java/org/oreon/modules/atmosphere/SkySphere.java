package org.oreon.modules.atmosphere;

import org.oreon.core.scene.Node;
import org.oreon.core.utils.Constants;

public class SkySphere extends Node{

	public SkySphere()
	{
		Skydome top = new Skydome();
		top.getWorldTransform().setLocalScaling(Constants.ZFAR*0.5f, Constants.ZFAR*0.5f, Constants.ZFAR*0.5f);
		
		Skydome bot = new Skydome();
		bot.getWorldTransform().setLocalTranslation(0,-30,0);
		bot.getWorldTransform().setLocalScaling(Constants.ZFAR*0.5f, -Constants.ZFAR*0.5f, Constants.ZFAR*0.5f);
		
		addChild(top);
		addChild(bot);
	}
}
