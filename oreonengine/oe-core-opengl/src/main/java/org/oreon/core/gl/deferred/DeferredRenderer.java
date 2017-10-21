package org.oreon.core.gl.deferred;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.nio.ByteBuffer;

import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.system.CoreSystem;

public class DeferredRenderer {

	private GBuffer gbuffer;
	private DeferredShader shader;
	private Texture2D deferredSceneTexture;
	private int width;
	private int height;
	
	public DeferredRenderer(int width, int height) {
		
		this.width = width;
		this.height = height;
		
		this.gbuffer = new GBuffer(width, height);
		this.shader = DeferredShader.getInstance();

		deferredSceneTexture = new Texture2D();
		deferredSceneTexture.generate();
		deferredSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		deferredSceneTexture.bilinearFilter();
		deferredSceneTexture.clampToEdge();
	}
	
	public void render(){
		
		shader.bind();
		glBindImageTexture(0, deferredSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(1, gbuffer.getAlbedoTexture().getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA8);
		glBindImageTexture(2, gbuffer.getWorldPositionTexture().getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(3, gbuffer.getNormalTexture().getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(4, gbuffer.getSpecularEmissionTexture().getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA8);
		shader.updateUniforms();
		glDispatchCompute(CoreSystem.getInstance().getWindow().getWidth()/16, CoreSystem.getInstance().getWindow().getHeight()/16,1);
		glFinish();
		deferredSceneTexture.bind();
		deferredSceneTexture.bilinearFilter();
	}

	public GBuffer getGbuffer() {
		return gbuffer;
	}

	public void setGbuffer(GBuffer gbuffer) {
		this.gbuffer = gbuffer;
	}

	public Texture2D getDeferredSceneTexture() {
		return deferredSceneTexture;
	}

	public void setDeferredSceneTexture(Texture2D deferredSceneTexture) {
		this.deferredSceneTexture = deferredSceneTexture;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
