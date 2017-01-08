package editor.tools.terrainEditor;

import java.awt.Canvas;

import editor.db.DB;
import editor.terrain.TerrainGridShader;
import editor.terrain.TerrainShader;
import editor.terrain.TerrainShadowShader;
import engine.core.Game;
import modules.gui.GUIs.GridFPS;
import modules.sky.SkySphere;
import modules.terrain.Terrain;

public class OpenGLEngine implements Runnable{
	
	Canvas OpenGLCanvas;
	
	public OpenGLEngine(Canvas canvas){
		OpenGLCanvas = canvas;
	}

	@Override
	public void run() {
		
		Game game = new Game();
		game.setGui(new GridFPS());
		game.getEngine().embedWindow(1000, 520, OpenGLCanvas);
		game.init();
		game.getScenegraph().setTerrain(Terrain.getInstance());
		Terrain.getInstance().init("./res/editor/terrainEditor/terrainSettings.ter", TerrainShader.getInstance(),
				TerrainGridShader.getInstance(), TerrainShadowShader.getInstance());
		DB.setTerrainConfiguration(Terrain.getInstance().getTerrainConfiguration());
		game.getScenegraph().addObject(new SkySphere());	
		game.launch();
	}
}
