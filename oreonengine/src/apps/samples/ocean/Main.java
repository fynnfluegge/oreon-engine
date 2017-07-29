package apps.samples.ocean;

import engine.core.Game;
import modules.atmosphere.SkySphere;
import modules.atmosphere.Sun;
import modules.gui.GUIs.VoidGUI;

public class Main {

	public static void main(String[] args) {
		Game game = new Game();
		game.setGui(new VoidGUI());
		game.getEngine().createWindow(1920, 1080, "Ocean");
		game.init();
		game.getScenegraph().addObject(new SkySphere());
		game.getScenegraph().addObject(new Sun());
		game.getScenegraph().setWater(new Ocean());
		game.launch();
	}
}
