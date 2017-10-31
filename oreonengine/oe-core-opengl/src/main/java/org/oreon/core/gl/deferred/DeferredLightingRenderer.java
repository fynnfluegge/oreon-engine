package org.oreon.core.gl.deferred;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT3;
import static org.lwjgl.opengl.GL30.GL_R32F;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.oreon.core.gl.buffers.GLFramebuffer;
import org.oreon.core.gl.shaders.DeferredShader;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.BufferUtil;

public class DeferredLightingRenderer {

	private GLFramebuffer fbo;
	private GBuffer gbuffer;
	private DeferredShader shader;
	private Texture2D deferredSceneTexture;
	private Texture2D depthmap;
	
	public DeferredLightingRenderer(int width, int height) {
		
		gbuffer = new GBuffer(width, height);
		shader = DeferredShader.getInstance();

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
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(4);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.put(GL_COLOR_ATTACHMENT2);
		drawBuffers.put(GL_COLOR_ATTACHMENT3);
		drawBuffers.flip();
		
		fbo = new GLFramebuffer();
		fbo.bind();
		fbo.createColorTextureMultisampleAttachment(gbuffer.getAlbedoTexture().getId(),0);
		fbo.createColorTextureMultisampleAttachment(gbuffer.getWorldPositionTexture().getId(),1);
		fbo.createColorTextureMultisampleAttachment(gbuffer.getNormalTexture().getId(),2);
		fbo.createColorTextureMultisampleAttachment(gbuffer.getSpecularEmissionTexture().getId(),3);
		fbo.createDepthTextureMultisampleAttachment(gbuffer.getDepthTexture().getId());
		fbo.setDrawBuffers(drawBuffers);
		fbo.checkStatus();
		fbo.unbind();
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
		shader.updateUniforms(gbuffer.getDepthTexture());
		glDispatchCompute(CoreSystem.getInstance().getWindow().getWidth()/16, CoreSystem.getInstance().getWindow().getHeight()/16,1);
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

	public Texture2D getDepthmap() {
		return depthmap;
	}

	public void setDepthmap(Texture2D depthmap) {
		this.depthmap = depthmap;
	}

	public GLFramebuffer getFbo() {
		return fbo;
	}

	public void setFbo(GLFramebuffer fbo) {
		this.fbo = fbo;
	}
}
