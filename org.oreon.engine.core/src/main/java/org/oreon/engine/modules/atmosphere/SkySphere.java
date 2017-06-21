package org.oreon.engine.modules.atmosphere;

import org.oreon.engine.engine.scenegraph.Node;
import org.oreon.engine.engine.utils.Constants;

public class SkySphere extends Node{

	public SkySphere()
	{
		Skydome top = new Skydome();
		top.getTransform().setLocalScaling(Constants.ZFAR*0.5f, Constants.ZFAR*0.5f, Constants.ZFAR*0.5f);
		
		Skydome bot = new Skydome();
		bot.getTransform().setLocalTranslation(0,-30,0);
		bot.getTransform().setLocalScaling(Constants.ZFAR*0.5f, -Constants.ZFAR*0.5f, Constants.ZFAR*0.5f);
		
		addChild(top);
		addChild(bot);
	}
}
