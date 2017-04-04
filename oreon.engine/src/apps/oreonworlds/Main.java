package apps.oreonworlds;

import apps.oreonworlds.assets.plants.Grass01;
import apps.oreonworlds.assets.plants.Palm;
import apps.oreonworlds.assets.plants.Plant01;
import apps.oreonworlds.assets.plants.Tree01;
import apps.oreonworlds.assets.plants.Tree02ClusterGroup;
import apps.oreonworlds.assets.rocks.Rock01;
import apps.oreonworlds.assets.rocks.Rock02;
import apps.oreonworlds.gui.GUI;
import apps.oreonworlds.shaders.terrain.TerrainGridShader;
import apps.oreonworlds.shaders.terrain.TerrainShader;
import apps.oreonworlds.water.Ocean;
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
//		game.getScenegraph().getRoot().addChild(new Palm());
//		game.getScenegraph().getRoot().addChild(new Plant01());
//		game.getScenegraph().getRoot().addChild(new Grass01());
//		game.getScenegraph().getRoot().addChild(new Tree01());
//		game.getScenegraph().getRoot().addChild(new Tree02ClusterGroup());
//		game.getScenegraph().getRoot().addChild(new Rock01());
//		game.getScenegraph().getRoot().addChild(new Rock02());
		game.getScenegraph().setWater(new Ocean());
		game.launch();
	}

}
