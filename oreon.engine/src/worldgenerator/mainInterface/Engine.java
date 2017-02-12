package worldgenerator.mainInterface;

import java.awt.Canvas;

import engine.core.Game;
import modules.gui.GUIs.GridFPS;
import modules.sky.SkySphere;
import modules.terrain.Terrain;
import worldgenerator.db.DB;
import worldgenerator.tools.terrainEditor.TerrainGridShader;
import worldgenerator.tools.terrainEditor.TerrainShader;
import worldgenerator.tools.terrainEditor.TerrainShadowShader;

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
