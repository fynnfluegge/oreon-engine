package modules.gui.GUIs;

import modules.gui.GUI;
import modules.gui.GUIElement;
import modules.gui.Screen;
import modules.gui.elements.FPSPanel;
import modules.gui.elements.GridButton;


public class GridFPS extends GUI{
	
	public void init() {
		Screen screen0 = new Screen();
		screen0.setElements(new GUIElement[2]);
		screen0.getElements()[0] = new FPSPanel();
		screen0.getElements()[0].init();
		screen0.getElements()[1] = new GridButton();
		screen0.getElements()[1].init();
		screen0.init();
		getScreens().add(screen0);
	}
}
