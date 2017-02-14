package oreonworlds;

import engine.core.Game;
import modules.sky.SkySphere;
import modules.terrain.Terrain;
import oreonworlds.assets.plants.Bush01Instanced;
import oreonworlds.assets.plants.Grass01Instanced;
import oreonworlds.assets.plants.Palm01Instanced;
import oreonworlds.assets.rocks.Rock01Instanced;
import oreonworlds.assets.rocks.Rock02Instanced;
import oreonworlds.gui.GUI;
import oreonworlds.shaders.terrain.TerrainGridShader;
import oreonworlds.shaders.terrain.TerrainShader;
import worldgenerator.tools.terrainEditor.TerrainShadowShader;

public class Main {

	public static void main(String[] args) {
		
		Game game = new Game();
		game.setGui(new GUI());
		game.getEngine().createWindow(1920, 1080, "oreon worlds");
		game.init();
		game.getScenegraph().setTerrain(Terrain.getInstance());
		Terrain.getInstance().init("./res/oreonworlds/terrain_settings.txt", TerrainShader.getInstance(),
				TerrainGridShader.getInstance(), null);
		game.getScenegraph().addObject(new SkySphere());	
		//game.getScenegraph().getRoot().addChild(new Bush01Instanced());
		//game.getScenegraph().getRoot().addChild(new Palm01Instanced());
		game.getScenegraph().getRoot().addChild(new Rock01Instanced());
		game.getScenegraph().getRoot().addChild(new Rock02Instanced());
		game.getScenegraph().getRoot().addChild(new Grass01Instanced());
		game.launch();
	}

}
