package org.oreon.gl.demo.oreonworlds.gui;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.math.Vec2f;
import org.oreon.gl.components.ui.elements.Button;

public class WireframeButton extends Button{
	
	public WireframeButton()
	{
		buttonMap = new Texture2D("gui/tex/buttons.png");
		buttonMap.bind();
		buttonMap.bilinearFilter();
		buttonClickMap = new Texture2D("gui/tex/buttonsClicked.png");
		buttonClickMap.bind();
		buttonClickMap.bilinearFilter();
		getOrthoTransform().setTranslation(5, EngineContext.getWindow().getHeight()-60, 0);
		getOrthoTransform().setScaling(50, 25, 0);
		Vec2f[] texCoordsgb = new Vec2f[4];
		texCoordsgb[0] = new Vec2f(0,0.4f);
		texCoordsgb[1] = new Vec2f(0,0);
		texCoordsgb[2] = new Vec2f(1,0);
		texCoordsgb[3] = new Vec2f(1,0.4f);
		setTexCoords(texCoordsgb);
	}
	
	@Override
	public void onClickActionPerformed()
	{
		EngineContext.getConfig().setWireframe(!EngineContext.getConfig().isWireframe());
	}
}
