package org.oreon.examples.gl.oreonworlds.ocean;

import org.oreon.core.math.Vec4f;
import org.oreon.core.util.Constants;
import org.oreon.examples.gl.oreonworlds.shaders.OceanBRDFShader;
import org.oreon.examples.gl.oreonworlds.shaders.OceanWireframeShader;
import org.oreon.gl.components.water.Water;

public class Ocean extends Water{

	public Ocean() {
		super(128, OceanBRDFShader.getInstance(), OceanWireframeShader.getInstance());
		
		getWorldTransform().setScaling(Constants.ZFAR,1,Constants.ZFAR);
		getWorldTransform().setTranslation(-Constants.ZFAR/2,-40,-Constants.ZFAR/2);
		
		setClip_offset(4);
		setClipplane(new Vec4f(0,-1,0,getWorldTransform().getTranslation().getY() + getClip_offset()));
	}

}
