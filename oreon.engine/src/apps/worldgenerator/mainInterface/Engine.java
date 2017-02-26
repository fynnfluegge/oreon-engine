package apps.worldgenerator.mainInterface;

import java.awt.Canvas;

import apps.worldgenerator.db.DB;
import apps.worldgenerator.tools.terrainEditor.TerrainGridShader;
import apps.worldgenerator.tools.terrainEditor.TerrainShader;
import apps.worldgenerator.tools.terrainEditor.TerrainShadowShader;
import engine.core.Game;
import modules.gui.GUIs.GridFPS;
import modules.sky.SkySphere;
import modules.terrain.Terrain;

public class Engine implements Runnable{
	
	Canvas OpenGLCanvas;
	
	public Engine(Canvas canvas){
		OpenGLCanvas = canvas;
	}

	@Override
	public void run() {
		
		Game game = new Game();
		game.setGui(new GridFPS());
		game.getEngine().embedWindow(1000, 500, OpenGLCanvas);
		game.init();
		game.getScenegraph().setTerrain(Terrain.getInstance());
		Terrain.getInstance().init("./res/editor/terrainEditor/terrainSettings.ter", TerrainShader.getInstance(),
				TerrainGridShader.getInstance(), TerrainShadowShader.getInstance());
		DB.setTerrainConfiguration(Terrain.getInstance().getTerrainConfiguration());
		game.getScenegraph().addObject(new SkySphere());	
		game.launch();
	}
}
