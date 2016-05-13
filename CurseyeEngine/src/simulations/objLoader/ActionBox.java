package simulations.objLoader;

import modules.glass.GlassRenderer;
import modules.lighting.DirectionalLight;
import engine.main.RenderingEngine;
import engine.math.Vec3f;
import simulations.templates.BasicSimulation;

public class ActionBox extends BasicSimulation{

	public void init(){
		super.init();
		getRoot().addChild(new ActionBoxModel());
		getRoot().addChild(new OBJ());
//		getRoot().addChild(new Logo());
		RenderingEngine.setDirectionalLight(new DirectionalLight(new Vec3f(1,-1,0), new Vec3f(0.1f,0.1f,0.1f), new Vec3f(1.0f, 1.0f, 1.0f), 1f));
	}
	
	public void render(){
		super.render();
		GlassRenderer.getInstance().render();
	}
}
