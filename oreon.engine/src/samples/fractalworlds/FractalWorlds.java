package samples.fractalworlds;

import modules.sky.SkySphere;
import modules.terrain.Terrain;
import engine.main.Game;

public class FractalWorlds {
	
	public static void main(String[] args) {
		
		Game game = new Game();
		game.setGui(new GUI());
		game.getEngine().createWindow(1000, 800, "Fractalworlds");
		game.init();
		game.getScenegraph().setTerrain(Terrain.getInstance());
		Terrain.getInstance().init("./res/samples/FractalWorlds/Terrain/terrainSettings.ter",
				TerrainShader.getInstance(),
				TerrainGridShader.getInstance(),
				TerrainShadowShader.getInstance());
		game.getScenegraph().addObject(new SkySphere());
		game.getScenegraph().getRoot().addChild(new TestObject());
		game.launch();
	}
}
