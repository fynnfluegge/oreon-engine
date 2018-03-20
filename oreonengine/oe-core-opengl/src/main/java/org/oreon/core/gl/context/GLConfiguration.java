package org.oreon.core.gl.context;

import org.oreon.core.gl.buffers.GLFramebuffer;
import org.oreon.core.texture.Texture;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GLConfiguration {
	
	protected static GLConfiguration instance = null;
	
	public Texture sceneDepthMap;
	public GLFramebuffer deferredFbo;

	public Texture underwaterDudvMap;
	public Texture underwaterCausticsMap;
	public float underwaterDistortion;
	
	public static GLConfiguration getInstance(){
		
		if(instance == null){
			instance = new GLConfiguration();
		}
		return instance;
	}
	
}
