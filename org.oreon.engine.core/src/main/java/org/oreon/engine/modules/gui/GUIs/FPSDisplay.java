package org.oreon.engine.modules.gui.GUIs;

import org.oreon.engine.modules.gui.GUI;
import org.oreon.engine.modules.gui.GUIElement;
import org.oreon.engine.modules.gui.Screen;
import org.oreon.engine.modules.gui.elements.FPSPanel;

public class FPSDisplay extends GUI{
	
	public void init() {
		Screen screen0 = new Screen();
		screen0.setElements(new GUIElement[1]);
		screen0.getElements()[0] = new FPSPanel();
		getScreens().add(screen0);
	}
}
