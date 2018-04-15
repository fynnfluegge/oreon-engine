package org.oreon.gl.components.filter.contrast;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.lwjgl.glfw.GLFW;
import org.oreon.core.context.EngineContext;
import org.oreon.core.gl.pipeline.GLShaderProgram;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DBilinearFilterRGBA16F;

import lombok.Getter;

public class ContrastController {

	@Getter
	private GLTexture contrastTexture;
	private GLShaderProgram contrastShader;
	
	private float contrastFactor = 1.0f;
	private float brightnessFactor = 2.0f;
	
	public ContrastController() {
	
		contrastShader = ContrastShader.getInstance();
		
		contrastTexture = new Texture2DBilinearFilterRGBA16F(EngineContext.getWindow().getWidth(), EngineContext.getWindow().getHeight());
		contrastTexture.bind();
		contrastTexture.clampToEdge();
		contrastTexture.unbind();
	}
	
	public void render(GLTexture sceneSampler) {
		
		contrastShader.bind();
		glBindImageTexture(0, sceneSampler.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA16F);
		glBindImageTexture(1, contrastTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
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
	
}
