package org.oreon.gl.demo.oreonworlds.gui;

import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.math.Vec2f;
import org.oreon.core.system.CoreSystem;
import org.oreon.modules.gl.gui.elements.Button;

public class GridButton extends Button{
	
	public GridButton()
	{
		buttonMap = new Texture2D("gui/tex/buttons.png");
		buttonMap.bind();
		buttonMap.bilinearFilter();
		buttonClickMap = new Texture2D("gui/tex/buttonsClicked.png");
		buttonClickMap.bind();
		buttonClickMap.bilinearFilter();
		getOrthoTransform().setTranslation(5, CoreSystem.getInstance().getWindow().getHeight()-60, 0);
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
		CoreSystem.getInstance().getRenderEngine().setGrid(!CoreSystem.getInstance().getRenderEngine().isGrid());
	}
}
