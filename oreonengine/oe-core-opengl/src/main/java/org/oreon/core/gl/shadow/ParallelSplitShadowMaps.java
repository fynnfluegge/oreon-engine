package org.oreon.core.gl.shadow;

import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL42.glTexStorage3D;

import org.oreon.core.configs.RenderConfig;
import org.oreon.core.gl.buffers.GLFramebuffer;
import org.oreon.core.gl.config.ShadowConfig;
import org.oreon.core.gl.texture.Texture2DArray;
import org.oreon.core.util.Constants;

import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

public class ParallelSplitShadowMaps {

	private GLFramebuffer fbo;
	private Texture2DArray depthMaps;
	private RenderConfig config;

	public ParallelSplitShadowMaps(){
		
		config = new ShadowConfig();
		
		depthMaps = new Texture2DArray();
		depthMaps.generate();
		depthMaps.bind();
		glTexStorage3D(GL_TEXTURE_2D_ARRAY,
					1,
					GL_DEPTH_COMPONENT32F,
					Constants.PSSM_SHADOWMAP_RESOLUTION,
					Constants.PSSM_SHADOWMAP_RESOLUTION,
					Constants.PSSM_SPLITS);

		depthMaps.bilinearFilter();
		depthMaps.clampToEdge();
		depthMaps.unbind();
		
		fbo = new GLFramebuffer();
		fbo.bind();
		glFramebufferTexture(GL_FRAMEBUFFER,
				GL_DEPTH_ATTACHMENT,
				depthMaps.getId(),
				0);
		glDrawBuffers(GL_NONE);
		fbo.checkStatus();
		fbo.unbind();	
	}
	
	public GLFramebuffer getFBO(){
		return fbo;
	}
	public Texture2DArray getDepthMaps(){
		return depthMaps;
	}

	public RenderConfig getConfig() {
		return config;
	}
	
	public void setConfig(RenderConfig config) {
		this.config = config;
	}
}
