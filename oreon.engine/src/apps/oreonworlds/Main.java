package apps.oreonworlds;

import apps.oreonworlds.assets.plants.Palm;
import apps.oreonworlds.assets.plants.PalmInstanced;
import apps.oreonworlds.assets.plants.Plant01Instanced;
import apps.oreonworlds.assets.plants.Tree01;
import apps.oreonworlds.gui.GUI;
import apps.oreonworlds.shaders.terrain.TerrainGridShader;
import apps.oreonworlds.shaders.terrain.TerrainShader;
import apps.worldgenerator.tools.terrainEditor.TerrainShadowShader;
import engine.core.Game;
import modules.sky.SkySphere;
import modules.terrain.Terrain;

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
		game.getScenegraph().getRoot().addChild(new Tree01());
		game.launch();
	}

}
