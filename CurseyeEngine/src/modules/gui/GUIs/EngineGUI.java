package modules.gui.GUIs;

import modules.gui.Button;
import modules.gui.GUI;
import modules.gui.GUIElement;
import modules.gui.Screen;
import modules.gui.elements.FPSPanel;
import modules.gui.elements.GridButton;


public class EngineGUI extends GUI{
	
	public void init() {
		
		Screen screen0 = new Screen();
		Button gridButton = new GridButton();
		screen0.setElements(new GUIElement[2]);
		screen0.getElements()[0] = new FPSPanel();
		screen0.getElements()[1] = gridButton;
		screen0.init();
		getScreens().add(screen0);
	}
}
