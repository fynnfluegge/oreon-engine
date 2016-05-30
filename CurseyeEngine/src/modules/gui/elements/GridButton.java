package modules.gui.elements;

import modules.gui.Button;
import engine.core.Texture;
import engine.core.OpenGLWindow;
import engine.main.RenderingEngine;
import engine.math.Vec2f;

public class GridButton extends Button{
	
	public GridButton()
	{
		buttonMap = new Texture("./res/textures/gui/buttons.png");
		buttonClickMap = new Texture("./res/textures/gui/buttonsClicked.png");
		getOrthoTransform().setTranslation(5, OpenGLWindow.getHeight()-70, 0);
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
		RenderingEngine.setGrid(!RenderingEngine.isGrid());
	}

}
