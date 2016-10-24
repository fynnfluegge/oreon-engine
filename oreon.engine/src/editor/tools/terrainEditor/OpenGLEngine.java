package editor.tools.terrainEditor;

import java.awt.Canvas;

import editor.terrain.Terrain;
import modules.gui.GUIs.EngineGUI;
import modules.sky.SkySphere;
import engine.main.Game;

public class OpenGLEngine implements Runnable{
	
	Canvas OpenGLCanvas;
	
	public OpenGLEngine(Canvas canvas){
		OpenGLCanvas = canvas;
	}

	@Override
	public void run() {
		
		Game game = new Game(1200, 350, "TerrainLoader");
		game.setGui(new EngineGUI());
		game.getEngine().embedWindow(OpenGLCanvas);
		game.init();
		game.getScenegraph().setTerrain(new Terrain());
		game.getScenegraph().addObject(new SkySphere());	
		game.launch();
	}
}
