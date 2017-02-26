package apps.samples.fractalworlds;

import engine.core.Game;
import modules.sky.SkySphere;
import modules.terrain.Terrain;

public class FractalWorlds {
	
	public static void main(String[] args) {
		
		Game game = new Game();
		game.setGui(new GUI());
		game.getEngine().createWindow(1280, 720, "Fractalworlds");
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
