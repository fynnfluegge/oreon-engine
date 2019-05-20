package org.oreon.gl.engine.antialiasing;

import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.context.BaseContext;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.TextureImage2D;
import org.oreon.core.image.Image.ImageFormat;
import org.oreon.core.image.Image.SamplerFilter;
import org.oreon.core.image.Image.TextureWrapMode;

import lombok.Getter;

public class FXAA {

	private FXAAShader shader;
	@Getter
	private GLTexture fxaaSceneTexture;
	
	public FXAA(){
	
		shader = FXAAShader.getInstance();
		
		fxaaSceneTexture = new TextureImage2D(BaseContext.getWindow().getWidth(), BaseContext.getWindow().getHeight(),
				ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest, TextureWrapMode.None);
	}
	
	public void render(GLTexture sceneTexture){
		
		shader.bind();
		glBindImageTexture(0, fxaaSceneTexture.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		shader.updateUniforms(sceneTexture);
		glDispatchCompute(BaseContext.getWindow().getWidth()/16, BaseContext.getWindow().getHeight()/16, 1);	
	}

}
