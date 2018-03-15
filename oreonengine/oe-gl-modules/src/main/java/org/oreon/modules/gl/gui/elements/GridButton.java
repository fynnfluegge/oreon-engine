package org.oreon.modules.gl.gui.elements;

import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.math.Vec2f;
import org.oreon.core.system.CoreSystem;

public class GridButton extends Button{
	
	public GridButton()
	{
		buttonMap = new Texture2D("./res/gui/tex/buttons.png");
		buttonClickMap = new Texture2D("./res/gui/tex/buttonsClicked.png");
		getOrthoTransform().setTranslation(5, CoreSystem.getInstance().getWindow().getHeight()-60, 0);
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
		CoreSystem.getInstance().getRenderEngine().setGrid(!CoreSystem.getInstance().getRenderEngine().isWireframe());
	}

}
