package org.oreon.gl.demo.oreonworlds.gui;

import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DBilinearFilter;
import org.oreon.core.math.Vec2f;
import org.oreon.gl.components.ui.elements.Button;

public class OpenCloseButton extends Button{
	
	private boolean isClosed = true;
	
	private GLTexture openArrow;
	private GLTexture openArrowClicked;
	private GLTexture closeArrow;
	private GLTexture closeArrowClicked;
	
	public OpenCloseButton()
	{
		openArrow = new Texture2DBilinearFilter("gui/tex/open_arrow.png");
		openArrowClicked = new Texture2DBilinearFilter("gui/tex/open_arrow.png");
		closeArrow = new Texture2DBilinearFilter("gui/tex/close_arrow.png");
		closeArrowClicked = new Texture2DBilinearFilter("gui/tex/close_arrow.png");
		
		buttonMap = openArrow;
		buttonClickMap = openArrowClicked;
		getOrthoTransform().setTranslation(5, EngineContext.getWindow().getHeight()-25, 0);
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