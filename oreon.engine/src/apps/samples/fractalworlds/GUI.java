package apps.samples.fractalworlds;

import modules.gui.GUIElement;
import modules.gui.Screen;
import modules.gui.elements.FPSPanel;
import modules.gui.elements.GridButton;

public class GUI extends modules.gui.GUI{

	@Override
	public void init() {
		Screen screen0 = new Screen();
		screen0.setElements(new GUIElement[4]);
		screen0.getElements()[0] = new Pssm0GUIPanel();
		screen0.getElements()[0].init();
		screen0.getElements()[1] = new Pssm1GUIPanel();
		screen0.getElements()[1].init();
		screen0.getElements()[2] = new Pssm2GUIPanel();
		screen0.getElements()[2].init();
		screen0.getElements()[3] = new Pssm3GUIPanel();
		screen0.getElements()[3].init();
		Screen screen1 = new Screen();
		screen1.setElements(new GUIElement[2]);
		screen1.getElements()[0] = new FPSPanel();
		screen1.getElements()[0].init();
		screen1.getElements()[1] = new GridButton();
		screen1.getElements()[1].init();
		getScreens().add(screen0);
		getScreens().add(screen1);
	}

}
