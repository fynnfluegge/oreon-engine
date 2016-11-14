package xamples.ocean;

import modules.sky.SkySphere;
import engine.main.Game;

public class OceanSimulation {

	public static void main(String[] args) {
		Game game = new Game();
		game.setGui(new FFTMapGUI());
		game.getEngine().createWindow(1000, 600, "Ocean Simulation");
		game.init();
		game.getScenegraph().addObject(new SkySphere());
		game.getScenegraph().setWater(new Ocean());
		game.launch();
	}
}
