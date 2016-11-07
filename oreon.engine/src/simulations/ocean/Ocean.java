package simulations.ocean;

import modules.water.Water;

import org.lwjgl.input.Keyboard;

import engine.core.Constants;
import engine.core.Input;
import engine.main.RenderingEngine;
import engine.math.Quaternion;

public class Ocean extends Water{
	
	TexturePanel texture;

	public Ocean() {
		super(512,512);
		
		getTransform().setScaling(Constants.ZFAR,1,Constants.ZFAR);
		getTransform().setTranslation(-Constants.ZFAR/2,0,-Constants.ZFAR/2);
		
		setClipplane(new Quaternion(0,-1,0,getTransform().getTranslation().getY()));

		this.loadSettingsFile("./res/demos/Ocean/waterSettings.wat");
	}
	
	public void render(){
		super.render();
		TexturePanel.texture = getFft().getDy();
	}
	
	public void update(){
		super.update();
		if (Input.getKeyDown(Keyboard.KEY_G))
		{
			RenderingEngine.setGrid(!RenderingEngine.isGrid());
		}
	}

}
