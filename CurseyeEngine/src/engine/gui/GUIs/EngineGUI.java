package engine.gui.GUIs;

import engine.gui.Button;
import engine.gui.GUI;
import engine.gui.GUIElement;
import engine.gui.Screen;
import engine.gui.elements.FPSPanel;
import engine.gui.elements.GridButton;


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
