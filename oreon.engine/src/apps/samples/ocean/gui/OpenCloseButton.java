package apps.samples.ocean.gui;

import engine.core.Window;
import engine.math.Vec2f;
import engine.textures.Texture2D;
import modules.gui.Button;

public class OpenCloseButton extends Button{
	
	private boolean isClosed = true;
	
	public OpenCloseButton()
	{
		buttonMap = new Texture2D("./res/gui/tex/open_arrow.png");
		buttonClickMap = new Texture2D("./res/gui/tex/open_arrow.png");
		getOrthoTransform().setTranslation(5, Window.getInstance().getHeight()-50, 0);
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
		if (isClosed){
			buttonMap = new Texture2D("./res/gui/tex/close_arrow.png");
			buttonClickMap = new Texture2D("./res/gui/tex/close_arrow.png");
			isClosed = false;
		}
		else{
			buttonMap = new Texture2D("./res/gui/tex/open_arrow.png");
			buttonClickMap = new Texture2D("./res/gui/tex/open_arrow.png");
			isClosed = true;
		}
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}
}