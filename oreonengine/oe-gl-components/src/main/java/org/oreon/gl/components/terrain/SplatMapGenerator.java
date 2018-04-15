package org.oreon.gl.components.terrain;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DStorageRGBA16F;

import lombok.Getter;

public class SplatMapGenerator {
	
	@Getter
	private GLTexture splatmap;
	private SplatMapShader shader;
	private int N;
	
	public SplatMapGenerator(int N) {
		
		this.N = N;
		shader = SplatMapShader.getInstance();
		splatmap = new Texture2DStorageRGBA16F(N,N,(int) (Math.log(N)/Math.log(2)));
		splatmap.bind();
		splatmap.bilinearFilter();
		splatmap.unbind();
	}
	
	public void render(GLTexture normalmap){
		
		shader.bind();
		shader.updateUniforms(normalmap, N);
		glBindImageTexture(0, splatmap.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glDispatchCompute(N/16,N/16,1);
		glFinish();
		splatmap.bind();
		splatmap.bilinearFilter();
	}

}
