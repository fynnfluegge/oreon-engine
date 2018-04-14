package org.oreon.gl.components.ui.wrapper;

import org.oreon.gl.components.ui.GUI;
import org.oreon.gl.components.ui.GUIElement;
import org.oreon.gl.components.ui.Screen;
import org.oreon.gl.components.ui.elements.FPSPanel;

public class FPSDisplay extends GUI{
	
	public void init() {
		Screen screen0 = new Screen();
		screen0.setElements(new GUIElement[1]);
		screen0.getElements()[0] = new FPSPanel();
		getScreens().add(screen0);
	}
}
