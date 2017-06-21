package org.oreon.engine.modules.gui;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

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
		glDisable(GL_DEPTH_TEST);
		for (Screen screen: screens ){
			screen.render();
		}
		glEnable(GL_DEPTH_TEST);
	};
	
	public ArrayList<Screen> getScreens() {
		return screens;
	}
	public void setScreens(ArrayList<Screen> screens) {
		this.screens = screens;
	}
}
