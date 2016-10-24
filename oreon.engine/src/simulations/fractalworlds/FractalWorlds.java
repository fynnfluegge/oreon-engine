package simulations.fractalworlds;

import modules.gui.GUIs.VoidGUI;
import modules.sky.SkySphere;
import engine.main.Game;

public class FractalWorlds {
	
	public static void main(String[] args) {
		
		Game game = new Game(800, 600, "Fractalworlds");
		game.setGui(new VoidGUI());
		game.getEngine().createWindow();
		game.init();
		game.getScenegraph().setTerrain(new Terrain());
		game.getScenegraph().addObject(new SkySphere());
		game.launch();
	}
}
