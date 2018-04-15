package org.oreon.gl.components.water;

import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT2;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.nio.IntBuffer;

import org.oreon.core.gl.buffer.GLFramebuffer;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DBilinearFilterRGBA16F;
import org.oreon.core.util.BufferUtil;
import org.oreon.gl.components.water.shader.RefracReflecDeferredLightingShader;

import lombok.Getter;

public class RefracReflecRenderer {

	@Getter
	private GLTexture deferredLightingSceneTexture;
	
	private GLFramebuffer fbo;
	private RefracReflecGBuffer gbuffer;
	private RefracReflecDeferredLightingShader shader;
	
	private int width;
	private int height;
	
	public RefracReflecRenderer(int width, int height) {

		this.width = width;
		this.height = height;
		
		gbuffer = new RefracReflecGBuffer(width, height);
		shader = RefracReflecDeferredLightingShader.getInstance();
		
		deferredLightingSceneTexture = new Texture2DBilinearFilterRGBA16F(width, height);
		
		IntBuffer drawBuffers = BufferUtil.createIntBuffer(3);
		drawBuffers.put(GL_COLOR_ATTACHMENT0);
		drawBuffers.put(GL_COLOR_ATTACHMENT1);
		drawBuffers.put(GL_COLOR_ATTACHMENT2);
		drawBuffers.flip();
		
		fbo = new GLFramebuffer();
		fbo.bind();
		fbo.createColorTextureAttachment(gbuffer.getAlbedoTexture().getHandle(),0);
		fbo.createColorTextureAttachment(gbuffer.getNormalTexture().getHandle(),2);
		fbo.createDepthBufferAttachment(width, height);
		fbo.setDrawBuffers(drawBuffers);
		fbo.checkStatus();
		fbo.unbind();
	}
	
	public void render(){
		
		shader.bind();
		glBindImageTexture(0, deferredLightingSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(1, gbuffer.getAlbedoTexture().getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(2, gbuffer.getNormalTexture().getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		shader.updateUniforms();
		glDispatchCompute(width/8, height/8,1);
	}

	public GLFramebuffer getFbo() {
		return fbo;
	}

}
