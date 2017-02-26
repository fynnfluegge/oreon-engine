package apps.oreonworlds.gui;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import engine.core.Window;
import engine.math.Vec2f;
import engine.textures.Texture2D;
import modules.gui.Button;

public class FullScreenButton  extends Button{

	private boolean isFullScreenMode = false;
	
	public FullScreenButton()
	{
		buttonMap = new Texture2D("./res/gui/tex/maximize.png");
		buttonClickMap = new Texture2D("./res/gui/tex/maximize.png");
		getOrthoTransform().setTranslation(10, Window.getInstance().getHeight()-90, 0);
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
			try {
				Display.setFullscreen(true);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
			isFullScreenMode = true;
		}
		else {
			buttonMap = new Texture2D("./res/gui/tex/maximize.png");
			buttonClickMap = new Texture2D("./res/gui/tex/maximize.png");
			try {
				Display.setFullscreen(false);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
			isFullScreenMode = true;
			isFullScreenMode = false;
		}
	}
}
