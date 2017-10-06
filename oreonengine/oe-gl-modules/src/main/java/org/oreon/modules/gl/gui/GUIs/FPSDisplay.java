package org.oreon.modules.gl.gui.GUIs;

import org.oreon.modules.gl.gui.GUI;
import org.oreon.modules.gl.gui.GUIElement;
import org.oreon.modules.gl.gui.Screen;
import org.oreon.modules.gl.gui.elements.FPSPanel;

public class FPSDisplay extends GUI{
	
	public void init() {
		Screen screen0 = new Screen();
		screen0.setElements(new GUIElement[1]);
		screen0.getElements()[0] = new FPSPanel();
		getScreens().add(screen0);
	}
}
