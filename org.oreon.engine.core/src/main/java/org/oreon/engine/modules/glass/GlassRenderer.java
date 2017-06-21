package org.oreon.engine.modules.glass;

import org.oreon.engine.engine.scenegraph.GameObject;

public class GlassRenderer extends GameObject{
	
	private static GlassRenderer instance = null;
	
	public static GlassRenderer getInstance() 
	{
	    if(instance == null) 
	    {
	    	instance = new GlassRenderer();
	    }
	      return instance;
	}	
	
	public void render(){
		super.render();
		
		getChildren().clear();
	}

}
