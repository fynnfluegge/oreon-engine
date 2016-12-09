package oreonworlds;

import engine.main.Game;
import engine.math.Vec3f;
import modules.gui.GUIs.GridFPS;
import modules.sky.SkySphere;
import modules.terrain.Terrain;
import oreonworlds.plants.Palm;
import oreonworlds.plants.PalmBush;
import oreonworlds.shaders.TerrainGridShader;
import oreonworlds.shaders.TerrainShader;

public class Main {

	public static void main(String[] args) {
		
		Game game = new Game();
		game.setGui(new GridFPS());
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
		game.getScenegraph().getRoot().addChild(new PalmBush(new Vec3f(1160,Terrain.getInstance().getTerrainHeight(1160,-570),-570)));

		game.getScenegraph().getRoot().addChild(new Palm(new Vec3f(1110,Terrain.getInstance().getTerrainHeight(1110,-610),-610)));
		game.getScenegraph().getRoot().addChild(new Palm(new Vec3f(1140,Terrain.getInstance().getTerrainHeight(1140,-630),-630)));
		game.getScenegraph().getRoot().addChild(new Palm(new Vec3f(1160,Terrain.getInstance().getTerrainHeight(1160,-620),-620)));
		game.getScenegraph().getRoot().addChild(new Palm(new Vec3f(1090,Terrain.getInstance().getTerrainHeight(1090,-570),-570)));
		game.launch();
	}

}
