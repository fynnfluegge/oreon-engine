package engine.core;

import engine.scenegraph.Scenegraph;
import modules.gui.GUI;

public class Game {
	
	protected CoreEngine engine;
	private Scenegraph scenegraph;
	private GUI gui;
	
	public Game(){
		engine = new CoreEngine();
		scenegraph = new Scenegraph();
	}
	
	public void launch(){
		engine.start();
	}
	
	public void init(){
		engine.init(scenegraph, gui);
	}
	
	public CoreEngine getEngine() {
		return engine;
	}
	public void setEngine(CoreEngine engine) {
		this.engine = engine;
	}
	public Scenegraph getScenegraph() {
		return scenegraph;
	}
	public void setScenegraph(Scenegraph scenegraph) {
		this.scenegraph = scenegraph;
	}
	public GUI getGui() {
		return gui;
	}
	public void setGui(GUI gui) {
		this.gui = gui;
	}

}
