package oreonworlds;

import engine.core.Game;
import modules.sky.SkySphere;
import modules.terrain.Terrain;
import oreonworlds.assets.plants.Plant01Instanced;
import oreonworlds.assets.plants.Palm;
import oreonworlds.assets.plants.PalmInstanced;
import oreonworlds.gui.GUI;
import oreonworlds.shaders.terrain.TerrainGridShader;
import oreonworlds.shaders.terrain.TerrainShader;
import worldgenerator.tools.terrainEditor.TerrainShadowShader;

public class Main {

	public static void main(String[] args) {
		
		Game game = new Game();
		game.setGui(new GUI());
		game.getEngine().createWindow(1280, 720, "oreon worlds");
		game.init();
		game.getScenegraph().setTerrain(Terrain.getInstance());
		Terrain.getInstance().init("./res/oreonworlds/terrain/terrain_settings.txt", TerrainShader.getInstance(),
				TerrainGridShader.getInstance(), TerrainShadowShader.getInstance());
		game.getScenegraph().addObject(new SkySphere());	
		game.getScenegraph().getRoot().addChild(new Palm());
		game.launch();
	}

}
