package org.oreon.modules.gl.water;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT3;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT4;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.oreon.core.gl.buffers.GLFramebuffer;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.util.BufferUtil;
import org.oreon.modules.gl.water.shader.RefracReflecDeferredLightingShader;

public class RefracReflecRenderer {

	private GLFramebuffer fbo;
	private RefracReflecGBuffer gbuffer;
	private RefracReflecDeferredLightingShader shader;
	private Texture2D deferredLightingSceneTexture;
	
	private int width;
	private int height;
	
	public RefracReflecRenderer(int width, int height) {

		this.width = width;
		this.height = height;
		
		gbuffer = new RefracReflecGBuffer(width, height);
		shader = RefracReflecDeferredLightingShader.getInstance();
		
		deferredLightingSceneTexture = new Texture2D();
		deferredLightingSceneTexture.generate();
		deferredLightingSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
				width,
				height,
				0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		deferredLightingSceneTexture.noFilter();
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(5);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.put(GL_COLOR_ATTACHMENT2);
		drawBuffers.flip();
		
		fbo = new GLFramebuffer();
		fbo.bind();
		fbo.createColorTextureAttachment(gbuffer.getAlbedoTexture().getId(),0);
		fbo.createColorTextureAttachment(gbuffer.getNormalTexture().getId(),2);
		fbo.createDepthBufferAttachment(width, height);
		fbo.setDrawBuffers(drawBuffers);
		fbo.checkStatus();
		fbo.unbind();
	}
	
	public void render(){
		
		shader.bind();
		glBindImageTexture(0, deferredLightingSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(1, gbuffer.getAlbedoTexture().getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, gbuffer.getNormalTexture().getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		shader.updateUniforms();
		glDispatchCompute(width/8, height/8,1);
	}

	public GLFramebuffer getFbo() {
		return fbo;
	}

	public Texture2D getDeferredLightingSceneTexture() {
		return deferredLightingSceneTexture;
	}
}
