package engine.main;

import engine.scenegraph.Scenegraph;

public abstract class Simulation{

	protected Scenegraph scenegraph;
	
	public void init(){
		scenegraph = new Scenegraph();
	};
	
	public void render(){};

	public Scenegraph getScenegraph() {
		return scenegraph;
	}

	public void setScenegraph(Scenegraph scenegraph) {
		this.scenegraph = scenegraph;
	}
}
