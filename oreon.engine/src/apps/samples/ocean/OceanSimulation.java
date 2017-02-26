package apps.samples.ocean;

import apps.samples.ocean.gui.GUI;
import engine.core.Game;
import modules.sky.SkySphere;

public class OceanSimulation {

	public static void main(String[] args) {
		Game game = new Game();
		game.setGui(new GUI());
		game.getEngine().createWindow(1920, 1080, "Ocean");
		game.init();
		game.getScenegraph().addObject(new SkySphere());
		game.getScenegraph().setWater(new Ocean());
		game.launch();
	}
}
