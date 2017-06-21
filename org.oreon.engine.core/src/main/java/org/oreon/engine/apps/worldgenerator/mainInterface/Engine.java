package org.oreon.engine.apps.worldgenerator.mainInterface;

import java.awt.Canvas;

import org.oreon.engine.apps.worldgenerator.db.DB;
import org.oreon.engine.apps.worldgenerator.tools.terrainEditor.TerrainGridShader;
import org.oreon.engine.apps.worldgenerator.tools.terrainEditor.TerrainShader;
import org.oreon.engine.apps.worldgenerator.tools.terrainEditor.TerrainShadowShader;
import org.oreon.engine.engine.core.Game;
import org.oreon.engine.modules.atmosphere.SkySphere;
import org.oreon.engine.modules.gui.GUIs.GridFPS;
import org.oreon.engine.modules.terrain.Terrain;

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
		Terrain.getInstance().init("./res/editor/terrainEditor/terrainSettings.ter", 
				"",
				TerrainShader.getInstance(),
				TerrainGridShader.getInstance(),
				TerrainShadowShader.getInstance());
				DB.setTerrainConfiguration(Terrain.getInstance().getConfiguration());
		game.getScenegraph().addObject(new SkySphere());	
		game.launch();
	}
}
