package org.oreon.gl.engine.transparency;

import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT1;

import java.nio.IntBuffer;

import org.oreon.core.gl.framebuffer.GLFrameBufferObject;
import org.oreon.core.gl.framebuffer.GLFramebuffer;
import org.oreon.core.gl.surface.FullScreenQuad;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DBilinearFilterRGBA16F;
import org.oreon.core.gl.wrapper.texture.Texture2DNoFilterRGBA16F;
import org.oreon.core.target.FrameBufferObject.Attachment;
import org.oreon.core.util.BufferUtil;

public class OpaqueTransparencyBlending extends FullScreenQuad{
	
	private GLFrameBufferObject fbo;
	private OpaqueTransparencyBlendingShader shader;
	
	public OpaqueTransparencyBlending(int width, int height) {
		
		super();
		fbo = new OpaqueTransparencyBlendFbo(width, height);
		shader = OpaqueTransparencyBlendingShader.getInstance();
	}
	
	public void render(GLTexture opaqueScene, GLTexture opaqueSceneDepthMap,
			GLTexture opaqueSceneLightScatteringTexture,
			GLTexture transparencyLayer, GLTexture transparencyLayerDepthMap,
			GLTexture alphaMap, GLTexture transparencyLayerLightScatteringTexture){
		
		fbo.bind();
		getConfig().enable();
		shader.bind();
		shader.updateUniforms(opaqueScene, opaqueSceneDepthMap, opaqueSceneLightScatteringTexture, 
							  transparencyLayer, transparencyLayerDepthMap,
							  alphaMap, transparencyLayerLightScatteringTexture);
		getVao().draw();
		getConfig().disable();
		fbo.unbind();
	}
	
	public GLTexture getAttachment(Attachment attachment){
		return fbo.getAttachmentTexture(attachment);
	}
	
	private class OpaqueTransparencyBlendFbo extends GLFrameBufferObject{
		
		public OpaqueTransparencyBlendFbo(int width, int height) {
			
			GLTexture sceneTexture = new Texture2DBilinearFilterRGBA16F(width, height);
			GLTexture lightScatteringTexture = new Texture2DNoFilterRGBA16F(width, height);
			
			attachments.put(Attachment.ALBEDO, sceneTexture);
			attachments.put(Attachment.LIGHT_SCATTERING, lightScatteringTexture);
			
			IntBuffer drawBuffers = BufferUtil.createIntBuffer(2);
			drawBuffers.put(GL_COLOR_ATTACHMENT0);
			drawBuffers.put(GL_COLOR_ATTACHMENT1);
			drawBuffers.flip();
			
			frameBuffer = new GLFramebuffer();
			frameBuffer.bind();
			frameBuffer.createColorTextureAttachment(sceneTexture.getHandle(),0);
			frameBuffer.createColorTextureAttachment(lightScatteringTexture.getHandle(),1);
			frameBuffer.setDrawBuffers(drawBuffers);
			frameBuffer.checkStatus();
			frameBuffer.unbind();
		}
	}
}
