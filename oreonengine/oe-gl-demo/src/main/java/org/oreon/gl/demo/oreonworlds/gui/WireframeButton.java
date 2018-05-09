package org.oreon.gl.demo.oreonworlds.gui;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.wrapper.texture.Texture2DBilinearFilter;
import org.oreon.core.math.Vec2f;
import org.oreon.gl.components.ui.elements.Button;

public class WireframeButton extends Button{
	
	public WireframeButton()
	{
		buttonMap = new Texture2DBilinearFilter("gui/tex/buttons.png");
		buttonClickMap = new Texture2DBilinearFilter("gui/tex/buttonsClicked.png");
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
		EngineContext.getRenderState().setWireframe(
				!EngineContext.getRenderState().isWireframe());
	}
}
