package oreonworlds;

import engine.main.Game;
import engine.math.Vec3f;
import modules.gui.GUIs.EngineGUI;
import modules.sky.SkySphere;
import modules.terrain.Terrain;
import oreonworlds.plants.PalmBush;
import oreonworlds.shaders.TerrainGridShader;
import oreonworlds.shaders.TerrainShader;

public class Main {

	public static void main(String[] args) {
		
		Game game = new Game();
		game.setGui(new EngineGUI());
		game.getEngine().createWindow(1900, 1080, "oreon worlds");
		game.init();
		game.getScenegraph().setTerrain(Terrain.getInstance());
		Terrain.getInstance().init("./res/oreonworlds/terrain_settings.txt", TerrainShader.getInstance(),
				TerrainGridShader.getInstance(), null);
		game.getScenegraph().addObject(new SkySphere());	
		game.getScenegraph().getRoot().addChild(new PalmBush(new Vec3f(1100,Terrain.getInstance().getTerrainHeight(1100,-600),-600)));
		game.getScenegraph().getRoot().addChild(new PalmBush(new Vec3f(1100,Terrain.getInstance().getTerrainHeight(1100,-580),-580)));
		game.getScenegraph().getRoot().addChild(new PalmBush(new Vec3f(1110,Terrain.getInstance().getTerrainHeight(1110,-590),-590)));
		game.getScenegraph().getRoot().addChild(new PalmBush(new Vec3f(1120,Terrain.getInstance().getTerrainHeight(1120,-590),-590)));

//		game.getScenegraph().getRoot().addChild(new Palm(new Vec3f(1110,Terrain.getInstance().getTerrainHeight(1110,-610),-610)));
		game.launch();
	}

}
