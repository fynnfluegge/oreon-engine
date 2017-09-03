package org.oreon.demo.ocean;

import org.oreon.modules.gui.GUIElement;
import org.oreon.modules.gui.Screen;

public class GUI extends org.oreon.modules.gui.GUI{
	
	@Override
	public void init() {
		Screen screen0 = new Screen();
		screen0.setElements(new GUIElement[1]);
		TexturePanel panel =  new TexturePanel();
		screen0.getElements()[0] = panel;
		screen0.init();
		getScreens().add(screen0);
	}
}
