package org.oreon.gl.demo.oreonworlds.gui;

import org.oreon.common.ui.UIElement;
import org.oreon.common.ui.UIScreen;
import org.oreon.gl.components.ui.GLGUI;
import org.oreon.gl.components.ui.GLStaticTextPanel;

public class GUI extends GLGUI{

	@Override
	public void init() {
		super.init();
		UIScreen screen0 = new UIScreen();
		screen0.setElements(new UIElement[1]);
		screen0.getElements()[0] = new GLStaticTextPanel("abc", 100, 100, 100, 100, fontsTexture);
		getScreens().add(screen0);
	}
	
}
