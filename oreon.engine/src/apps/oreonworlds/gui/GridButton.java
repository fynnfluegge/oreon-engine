package apps.oreonworlds.gui;

import engine.core.Window;
import engine.core.RenderingEngine;
import engine.math.Vec2f;
import engine.textures.Texture2D;
import modules.gui.elements.Button;

public class GridButton extends Button{
	
	public GridButton()
	{
		buttonMap = new Texture2D("./res/gui/tex/buttons.png");
		buttonMap.bind();
		buttonMap.bilinearFilter();
		buttonClickMap = new Texture2D("./res/gui/tex/buttonsClicked.png");
		buttonClickMap.bind();
		buttonClickMap.bilinearFilter();
		getOrthoTransform().setTranslation(5, Window.getInstance().getHeight()-60, 0);
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
		RenderingEngine.setGrid(!RenderingEngine.isGrid());
	}
}
