package org.oreon.gl.components.ui.wrapper;

import org.oreon.gl.components.ui.GUI;
import org.oreon.gl.components.ui.GUIElement;
import org.oreon.gl.components.ui.Screen;
import org.oreon.gl.components.ui.elements.FPSPanel;
import org.oreon.gl.components.ui.elements.WireframeButton;


public class GridFPS extends GUI{
	
	public void init() {
		Screen screen0 = new Screen();
		screen0.setElements(new GUIElement[2]);
		screen0.getElements()[0] = new FPSPanel();
		screen0.getElements()[1] = new WireframeButton();
		getScreens().add(screen0);
	}
}
