package org.oreon.demo.gl.oreonworlds.gui;

import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.math.Vec2f;
import org.oreon.core.system.CoreSystem;
import org.oreon.modules.gl.gui.elements.Button;

public class FullScreenButton  extends Button{

	private boolean isFullScreenMode = false;
	
	
	
	public FullScreenButton()
	{
		buttonMap = new Texture2D("./res/gui/tex/maximize.png");
		buttonClickMap = new Texture2D("./res/gui/tex/maximize.png");
		getOrthoTransform().setTranslation(10, CoreSystem.getInstance().getWindow().getHeight()-90, 0);
		getOrthoTransform().setScaling(15,30,0);
		Vec2f[] texCoords = new Vec2f[4];
		texCoords[0] = new Vec2f(0,1f);
		texCoords[1] = new Vec2f(0,0);
		texCoords[2] = new Vec2f(1,0);
		texCoords[3] = new Vec2f(1,1f);
		setTexCoords(texCoords);
	}
	
	@Override
	public void onClickActionPerformed()
	{
		if (!isFullScreenMode){
			buttonMap = new Texture2D("./res/gui/tex/minimize.png");
			buttonClickMap = new Texture2D("./res/gui/tex/minimize.png");
//			Display.setFullscreen(true);
			isFullScreenMode = true;
		}
		else {
			buttonMap = new Texture2D("./res/gui/tex/maximize.png");
			buttonClickMap = new Texture2D("./res/gui/tex/maximize.png");
//			Display.setFullscreen(false);
			isFullScreenMode = true;
			isFullScreenMode = false;
		}
	}
}
