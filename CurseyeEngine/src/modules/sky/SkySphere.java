package modules.sky;

import engine.core.Camera;
import engine.gameObject.GameObject;

public class SkySphere extends GameObject{

	
	public SkySphere()
	{
		Skydome top = new Skydome();
		top.getTransform().setLocalScaling(Camera.getInstance().getZFar()*0.5f, Camera.getInstance().getZFar()*0.5f, Camera.getInstance().getZFar()*0.5f);
		
		Skydome bot = new Skydome();
		bot.getTransform().setLocalTranslation(0,-30,0);
		bot.getTransform().setLocalScaling(Camera.getInstance().getZFar()*0.5f, -Camera.getInstance().getZFar()*0.5f, Camera.getInstance().getZFar()*0.5f);
		
		addChild(top);
		addChild(bot);
	}
}
