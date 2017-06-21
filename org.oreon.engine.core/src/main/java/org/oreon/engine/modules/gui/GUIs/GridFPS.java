package org.oreon.engine.modules.gui.GUIs;

import org.oreon.engine.modules.gui.GUI;
import org.oreon.engine.modules.gui.GUIElement;
import org.oreon.engine.modules.gui.Screen;
import org.oreon.engine.modules.gui.elements.FPSPanel;
import org.oreon.engine.modules.gui.elements.GridButton;


public class GridFPS extends GUI{
	
	public void init() {
		Screen screen0 = new Screen();
		screen0.setElements(new GUIElement[2]);
		screen0.getElements()[0] = new FPSPanel();
		screen0.getElements()[1] = new GridButton();
		getScreens().add(screen0);
	}
}
