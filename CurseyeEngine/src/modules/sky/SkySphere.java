package modules.sky;

import engine.core.Constants;
import engine.scenegraph.Node;

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
