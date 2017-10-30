package org.oreon.core.gl.deferred;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_R32F;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.nio.ByteBuffer;

import org.oreon.core.gl.shaders.DeferredShader;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.system.CoreSystem;

public class DeferredRenderer {

	private GBuffer gbuffer;
	private DeferredShader shader;
	private Texture2D deferredSceneTexture;
	private Texture2D depthmap;
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
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
				width,
				height,
				0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		deferredSceneTexture.noFilter();
		
		depthmap = new Texture2D();
		depthmap.generate();
		depthmap.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F,
				width,
				height,
				0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		depthmap.noFilter();
	}
	
	public void render(Texture2D sampleCoverageMask){
		
		shader.bind();
		glBindImageTexture(0, deferredSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(1, depthmap.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		glBindImageTexture(2, gbuffer.getAlbedoTexture().getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(3, gbuffer.getWorldPositionTexture().getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(4, gbuffer.getNormalTexture().getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(5, gbuffer.getSpecularEmissionTexture().getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA8);
		glBindImageTexture(6, sampleCoverageMask.getId(), 0, false, 0, GL_READ_ONLY, GL_R32F);
		shader.updateUniforms(gbuffer.getDepthmap());
		glDispatchCompute(CoreSystem.getInstance().getWindow().getWidth()/16, CoreSystem.getInstance().getWindow().getHeight()/16,1);
	}
	
	public void renderTransparencyLayers(){
		
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

	public Texture2D getDepthmap() {
		return depthmap;
	}

	public void setDepthmap(Texture2D depthmap) {
		this.depthmap = depthmap;
	}
}
