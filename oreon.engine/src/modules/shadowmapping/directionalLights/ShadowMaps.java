package modules.shadowmapping.directionalLights;

import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL42.glTexStorage3D;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;


import engine.buffers.Framebuffer;
import engine.textures.Texture;
import engine.utils.Constants;

public class ShadowMaps {

	private Framebuffer fbo;
	private Texture depthMaps;
	
	public ShadowMaps(){
		depthMaps = new Texture();
		depthMaps.generate();
		glBindTexture(GL_TEXTURE_2D_ARRAY, depthMaps.getId());
		glTexStorage3D(GL_TEXTURE_2D_ARRAY,
					1,
					GL_DEPTH_COMPONENT32F,
					Constants.PSSM_SHADOWMAP_RESOLUTION,
					Constants.PSSM_SHADOWMAP_RESOLUTION,
					Constants.PSSM_SPLITS);

		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
		
		fbo = new Framebuffer();
		fbo.bind();
		glFramebufferTexture(GL_FRAMEBUFFER,
				GL_DEPTH_ATTACHMENT,
				depthMaps.getId(),
				0);
		glDrawBuffers(GL_NONE);
		fbo.checkStatus();
		fbo.unbind();	
	}
	
	public Framebuffer getFBO(){
		return fbo;
	}
	public Texture getDepthMaps(){
		return depthMaps;
	}
}
