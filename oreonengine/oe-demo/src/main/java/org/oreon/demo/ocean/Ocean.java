package org.oreon.demo.ocean;

import org.oreon.core.math.Quaternion;
import org.oreon.core.utils.Constants;
import org.oreon.modules.water.Water;
import org.oreon.modules.water.fft.Tilde_h0;
import org.oreon.modules.water.fft.Tilde_hkt;

public class Ocean extends Water{
	
	TexturePanel texture;

	public Ocean() {
		super(256,256);
		
		getWorldTransform().setScaling(Constants.ZFAR,1,Constants.ZFAR);
		getWorldTransform().setTranslation(-Constants.ZFAR/2,0,-Constants.ZFAR/2);
		
		setClipplane(new Quaternion(0,-1,0,getWorldTransform().getTranslation().getY()));

		this.loadSettingsFile("src/main/resources/ocean/waterSettings.txt");
	}
	
	public void render(){
		super.render();
//		TexturePanel.setTexture(((Tilde_h0) getFft().getFourierComponents().getSpectrum()).geth0kminus());
		TexturePanel.setTexture(getFft().getDy());
	}
}
