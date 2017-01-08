package samples.ocean;

import engine.core.Game;
import modules.sky.SkySphere;

public class OceanSimulation {

	public static void main(String[] args) {
		Game game = new Game();
		game.setGui(new FFTMapGUI());
		game.getEngine().createWindow(1280, 720, "Ocean");
		game.init();
		game.getScenegraph().addObject(new SkySphere());
		game.getScenegraph().setWater(new Ocean());
		game.launch();
	}
}
