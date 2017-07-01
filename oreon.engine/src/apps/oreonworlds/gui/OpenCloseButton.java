package apps.oreonworlds.gui;

import engine.core.Window;
import engine.math.Vec2f;
import engine.textures.Texture2D;
import modules.gui.elements.Button;

public class OpenCloseButton extends Button{
	
	private boolean isClosed = true;
	
	private Texture2D openArrow;
	private Texture2D openArrowClicked;
	private Texture2D closeArrow;
	private Texture2D closeArrowClicked;
	
	public OpenCloseButton()
	{
		openArrow = new Texture2D("./res/gui/tex/open_arrow.png");
		openArrow.bind();
		openArrow.bilinearFilter();
		openArrowClicked = new Texture2D("./res/gui/tex/open_arrow.png");
		openArrowClicked.bind();
		openArrowClicked.bilinearFilter();
		closeArrow = new Texture2D("./res/gui/tex/close_arrow.png");
		closeArrow.bind();
		closeArrow.bilinearFilter();
		closeArrowClicked = new Texture2D("./res/gui/tex/close_arrow.png");
		closeArrowClicked.bind();
		closeArrowClicked.bilinearFilter();
		
		buttonMap = openArrow;
		buttonClickMap = openArrowClicked;
		getOrthoTransform().setTranslation(5, Window.getInstance().getHeight()-25, 0);
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
			buttonMap = closeArrow;
			buttonClickMap = closeArrowClicked;
			isClosed = false;
		}
		else{
			buttonMap = openArrow;
			buttonClickMap = openArrowClicked; 
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