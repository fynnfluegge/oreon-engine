package org.oreon.core.system;

import org.oreon.core.platform.Camera;
import org.oreon.core.platform.Window;
import org.oreon.core.scenegraph.Scenegraph;

import lombok.Getter;

public abstract class RenderEngine {

	@Getter
	protected Scenegraph sceneGraph;
	
	protected Window window;
	protected Camera camera;
	
	public void init(){
		
		sceneGraph = new Scenegraph();
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
