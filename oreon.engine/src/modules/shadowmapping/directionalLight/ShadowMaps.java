package modules.shadowmapping.directionalLight;

import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL42.glTexStorage3D;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;
import engine.buffers.Framebuffer;
import engine.textures.Texture2DArray;
import engine.utils.Constants;

public class ShadowMaps {

	private Framebuffer fbo;
	private Texture2DArray depthMaps;
	
	public ShadowMaps(){
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
	public Texture2DArray getDepthMaps(){
		return depthMaps;
	}
}
