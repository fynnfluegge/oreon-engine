package simulations.objLoader;

import modules.gui.GUI;
import modules.gui.GUIs.VoidGUI;
import engine.main.CoreEngine;
import engine.main.Simulation;

public class ActionBox extends Simulation{

public static void main(String[] args) {
		
		Simulation simulation = new ActionBox();
		GUI gui = new VoidGUI();
		CoreEngine coreEngine = new CoreEngine(800, 800, "ActionBox");
		coreEngine.createWindow();
		coreEngine.init(simulation, gui);
		coreEngine.start();
	}
	
	public void init(){
		super.init();
		scenegraph.addObject(new ActionBoxModel());
		scenegraph.addObject(new OBJ());
//		scenegraph.addObject(new Logo());
//		scenegraph.addObject(GlassRenderer.getInstance());
	}
}
