package org.oreon.common.ui;

import java.util.ArrayList;

import lombok.Getter;

public abstract class GUI {

	@Getter
	private ArrayList<UIScreen> screens;
	
	public GUI(){
		screens = new ArrayList<UIScreen>();
	}
	
	public abstract void init();
	
	public void update(){
		for (UIScreen screen: screens ){
			screen.update();
		}
	};
	
	public void render(){
		
		for (UIScreen screen: screens ){
			screen.render();
		}
	};
	
}
