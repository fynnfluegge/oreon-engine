package oreonworlds;

import engine.core.Game;
import modules.gui.GUIs.GridFPS;
import modules.sky.SkySphere;
import modules.terrain.Terrain;
import oreonworlds.plants.Palm01Instanced;
import oreonworlds.plants.Bush01Instanced;
import oreonworlds.shaders.TerrainGridShader;
import oreonworlds.shaders.TerrainShader;

public class Main {

	public static void main(String[] args) {
		
		Game game = new Game();
		game.setGui(new GridFPS());
		game.getEngine().createWindow(1900, 1000, "oreon worlds");
		game.init();
		game.getScenegraph().setTerrain(Terrain.getInstance());
		Terrain.getInstance().init("./res/oreonworlds/terrain_settings.txt", TerrainShader.getInstance(),
				TerrainGridShader.getInstance(), null);
		game.getScenegraph().addObject(new SkySphere());	
		game.getScenegraph().getRoot().addChild(new Bush01Instanced());
		game.getScenegraph().getRoot().addChild(new Palm01Instanced());
		game.launch();
	}

}
