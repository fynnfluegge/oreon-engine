package org.oreon.core.system;

import org.oreon.core.context.Configuration;
import org.oreon.core.context.EngineContext;
import org.oreon.core.scenegraph.Camera;
import org.oreon.core.scenegraph.Scenegraph;

import lombok.Getter;

public abstract class RenderEngine {

	@Getter
	protected Scenegraph sceneGraph;
	protected Configuration config;
	protected Camera camera;
	
	public void init(){
		
		sceneGraph = new Scenegraph();
		config = EngineContext.getConfig();
		camera = EngineContext.getCamera();
	}
	public abstract void render();
	
	public void update(){
		
		sceneGraph.update();
		camera.update();
	}
	
	public void shutdown(){
		
		// important to shutdown scenegraph before render-engine, since
		// thread safety of instancing clusters.
		// scenegraph sets isRunning to false, render-engine signals all
		// waiting threads to shutdown
		
		sceneGraph.shutdown();
	}
	
}
