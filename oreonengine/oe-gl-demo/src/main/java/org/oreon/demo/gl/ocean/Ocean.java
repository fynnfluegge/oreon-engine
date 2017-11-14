package org.oreon.demo.gl.ocean;

import org.oreon.core.math.Quaternion;
import org.oreon.core.util.Constants;
import org.oreon.modules.gl.water.Water;
import org.oreon.modules.gl.water.shader.OceanBRDFShader;

public class Ocean extends Water{
	
	TexturePanel texture;

	public Ocean() {
		super(256,256,OceanBRDFShader.getInstance());
		
		getWorldTransform().setScaling(Constants.ZFAR,1,Constants.ZFAR);
		getWorldTransform().setTranslation(-Constants.ZFAR/2,0,-Constants.ZFAR/2);
		
		setClipplane(new Quaternion(0,-1,0,getWorldTransform().getTranslation().getY()));

		this.loadSettingsFile("src/main/resources/ocean/waterSettings.txt");
	}
	
	public void render(){
		super.render();
		TexturePanel.setTexture(getFft().getDy());
	}
}
