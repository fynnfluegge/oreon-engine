package org.oreon.gl.components.ui.elements;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.math.Vec2f;

public class WireframeButton extends Button{
	
	public WireframeButton()
	{
		buttonMap = new Texture2D("./res/gui/tex/buttons.png");
		buttonClickMap = new Texture2D("./res/gui/tex/buttonsClicked.png");
		getOrthoTransform().setTranslation(5, EngineContext.getWindow().getHeight()-60, 0);
		getOrthoTransform().setScaling(60, 40, 0);
		Vec2f[] texCoordsgb = new Vec2f[4];
		texCoordsgb[0] = new Vec2f(0,0.3f);
		texCoordsgb[1] = new Vec2f(0,0);
		texCoordsgb[2] = new Vec2f(1,0);
		texCoordsgb[3] = new Vec2f(1,0.3f);
		setTexCoords(texCoordsgb);
	}
	
	@Override
	public void onClickActionPerformed()
	{
		EngineContext.getConfig().setWireframe(!EngineContext.getConfig().isWireframe());
	}

}
