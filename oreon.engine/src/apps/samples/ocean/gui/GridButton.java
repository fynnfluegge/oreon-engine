package apps.samples.ocean.gui;

import modules.gui.Button;
import engine.core.Window;
import engine.core.RenderingEngine;
import engine.math.Vec2f;
import engine.textures.Texture2D;

public class GridButton extends Button{
	
	public GridButton()
	{
		buttonMap = new Texture2D("./res/gui/tex/buttons.png");
		buttonClickMap = new Texture2D("./res/gui/tex/buttonsClicked.png");
		getOrthoTransform().setTranslation(5, Window.getInstance().getHeight()-130, 0);
		getOrthoTransform().setScaling(50, 35, 0);
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
		RenderingEngine.setGrid(!RenderingEngine.isGrid());
	}

}
