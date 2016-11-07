package simulations.ocean;

import modules.gui.GUI;
import modules.gui.GUIElement;
import modules.gui.Screen;
import modules.gui.elements.FPSPanel;

public class FFTMapGUI extends GUI{

	@Override
	public void init() {
		Screen screen0 = new Screen();
		screen0.setElements(new GUIElement[2]);
		screen0.getElements()[0] = new TexturePanel();
		screen0.getElements()[1] = new FPSPanel();
		getScreens().add(screen0);
	}

}
