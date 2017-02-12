package samples.ocean;

import engine.core.Game;
import modules.sky.SkySphere;
import samples.ocean.gui.GUI;

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
