package org.oreon.common.ui;

import java.util.ArrayList;

import org.oreon.core.scenegraph.RenderList;

import lombok.Getter;

public abstract class GUI {

	@Getter
	private ArrayList<UIScreen> screens;
	
	public GUI(){
		screens = new ArrayList<UIScreen>();
	}
	
	public void update(){
		for (UIScreen screen: screens ){
			screen.update();
		}
	}
	
	public void render(){
		
		for (UIScreen screen: screens ){
			screen.render();
		}
	}
	
	public void record(RenderList renderList){

		for (UIScreen screen: screens ){
			screen.record(renderList);
		}
	}
	
}
