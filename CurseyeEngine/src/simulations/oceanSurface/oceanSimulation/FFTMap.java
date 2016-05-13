package simulations.oceanSurface.oceanSimulation;

import engine.gui.GUI;
import engine.gui.GUIElement;
import engine.gui.Screen;

public class FFTMap extends GUI{

	@Override
	public void init() {
		Screen screen0 = new Screen();
		screen0.setElements(new GUIElement[1]);
		screen0.getElements()[0] = new TexturePanel();
		screen0.getElements()[0].init();
		getScreens().add(screen0);
	}

}
