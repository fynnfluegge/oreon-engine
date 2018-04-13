package org.oreon.modules.gl.gpgpu;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFW;
import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.Texture2D;

public class ContrastController {

	private Texture2D contrastTexture;
	private GLShaderProgram contrastShader;
	
	private float contrastFactor = 1.0f;
	private float brightnessFactor = 2.0f;
	
	public ContrastController() {
	
		contrastShader = ContrastShader.getInstance();
		
		contrastTexture = new Texture2D();
		contrastTexture.generate();
		contrastTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F,
						EngineContext.getWindow().getWidth(),
						EngineContext.getWindow().getHeight(),
						0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		contrastTexture.bilinearFilter();
		contrastTexture.clampToEdge();
	}
	
	public void render(Texture2D sceneSampler) {
		
		contrastShader.bind();
		glBindImageTexture(0, sceneSampler.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, contrastTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		contrastShader.updateUniforms(contrastFactor, brightnessFactor);
		glDispatchCompute(EngineContext.getWindow().getWidth()/16, EngineContext.getWindow().getHeight()/16, 1);	
		glFinish();
	}
	
	public void update() {
		if (EngineContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_ADD)){
			contrastFactor += 0.01f;
		}
		if (EngineContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_SUBTRACT)){
			contrastFactor -= 0.01f;
		}
		if (EngineContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_MULTIPLY)){
			brightnessFactor += 1f;
		}
		if (EngineContext.getInput().isKeyPushed(GLFW.GLFW_KEY_KP_DIVIDE)){
			brightnessFactor -= 1f;
		}
	}

	public Texture2D getContrastTexture() {
		return contrastTexture;
	}
}
