package org.oreon.core.gl.system;

import org.oreon.core.buffers.Framebuffer;
import org.oreon.core.texture.Texture;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GLConfiguration {
	
	protected static GLConfiguration instance = null;
	
	public Texture sceneDepthMap;
	public Framebuffer deferredFbo;

	public Texture underwaterDudvMap;
	public Texture underwaterCausticsMap;
	public float underwaterDistortion;
	
	public static GLConfiguration getInstance(){
		
		if(instance == null){
			instance = new GLConfiguration();
		}
		return instance;
	}
	
	protected GLConfiguration(){}

}
