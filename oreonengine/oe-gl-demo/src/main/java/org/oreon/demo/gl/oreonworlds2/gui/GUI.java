package org.oreon.demo.gl.oreonworlds2.gui;

import org.oreon.modules.gl.gui.GUIElement;
import org.oreon.modules.gl.gui.Screen;

public class GUI extends org.oreon.modules.gl.gui.GUI{
	
	@Override
	public void init() {
		Screen screen0 = new Screen();
		screen0.setElements(new GUIElement[1]);
		screen0.getElements()[0] = new FPSPanel();
		screen0.init();
		getScreens().add(screen0);
		
	}
}
