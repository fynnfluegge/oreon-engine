package simulations.oceanSurface.oceanSimulation;

import modules.water.Water;

import org.lwjgl.input.Keyboard;

import engine.core.Constants;
import engine.core.Input;
import engine.main.RenderingEngine;
import engine.math.Quaternion;

public class Ocean extends Water{
	
	TexturePanel texture;

	public Ocean() {
		super(128);
		
		getTransform().setScaling(Constants.ZFAR,1,Constants.ZFAR);
		getTransform().setTranslation(-Constants.ZFAR/2,300,-Constants.ZFAR/2);
		
		setClipplane(new Quaternion(0,-1,0,getTransform().getTranslation().getY()));

		this.loadSettingsFile("./res/terrains/terrain0/waterSettings.ocn");
	}
	
	public void render(){
		super.render();
		TexturePanel.texture = getFft().getDy();
	}
	
	public void update(){
		super.update();
		if (Input.getKeyDown(Keyboard.KEY_G))
		{
			System.out.println("sf");
			RenderingEngine.setGrid(!RenderingEngine.isGrid());
		}
	}

}
