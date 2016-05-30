package modules.gui;

import java.util.ArrayList;

public abstract class GUI {

	private ArrayList<Screen> screens;
	
	public GUI(){
		screens = new ArrayList<Screen>();
	}
	
	public abstract void init();
	
	public void update(){
		for (Screen screen: screens ){
			screen.update();
		}
	};
	
	public void render(){
		for (Screen screen: screens ){
			screen.render();
		}
	};
	
	public ArrayList<Screen> getScreens() {
		return screens;
	}
	public void setScreens(ArrayList<Screen> screens) {
		this.screens = screens;
	}
}
