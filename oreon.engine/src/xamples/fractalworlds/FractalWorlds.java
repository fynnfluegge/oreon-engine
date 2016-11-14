package xamples.fractalworlds;

import modules.sky.SkySphere;
import engine.main.Game;

public class FractalWorlds {
	
	public static void main(String[] args) {
		
		Game game = new Game();
		game.setGui(new GUI());
		game.getEngine().createWindow(800, 600, "Fractalworlds");
		game.init();
		game.getScenegraph().setTerrain(new Terrain());
		game.getScenegraph().addObject(new SkySphere());
		game.launch();
	}
}
