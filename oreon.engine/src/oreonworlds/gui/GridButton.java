package oreonworlds.gui;

import modules.gui.Button;
import engine.core.OpenGLDisplay;
import engine.core.RenderingEngine;
import engine.math.Vec2f;
import engine.texturing.Texture;

public class GridButton extends Button{
	
	public GridButton()
	{
		buttonMap = new Texture("./res/textures/gui/buttons.png");
		buttonClickMap = new Texture("./res/textures/gui/buttonsClicked.png");
		getOrthoTransform().setTranslation(5, OpenGLDisplay.getInstance().getHeight()-110, 0);
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
