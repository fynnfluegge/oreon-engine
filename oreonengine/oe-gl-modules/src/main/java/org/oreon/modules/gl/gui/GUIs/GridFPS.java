package org.oreon.modules.gl.gui.GUIs;

import org.oreon.modules.gl.gui.GUI;
import org.oreon.modules.gl.gui.GUIElement;
import org.oreon.modules.gl.gui.Screen;
import org.oreon.modules.gl.gui.elements.FPSPanel;
import org.oreon.modules.gl.gui.elements.GridButton;


public class GridFPS extends GUI{
	
	public void init() {
		Screen screen0 = new Screen();
		screen0.setElements(new GUIElement[2]);
		screen0.getElements()[0] = new FPSPanel();
		screen0.getElements()[1] = new GridButton();
		getScreens().add(screen0);
	}
}
