package org.oreon.gl.components.terrain;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.TextureStorage2D;
import org.oreon.core.image.Image.ImageFormat;

import lombok.Getter;

public class SplatMapGenerator {
	
	@Getter
	private GLTexture splatmap;
	private SplatMapShader shader;
	private int N;
	
	public SplatMapGenerator(int N) {
		
		this.N = N;
		shader = SplatMapShader.getInstance();
		splatmap = new TextureStorage2D(N,N,(int) (Math.log(N)/Math.log(2)), ImageFormat.RGBA16FLOAT); 
		splatmap.bind();
		splatmap.bilinearFilter();
		splatmap.unbind();
	}
	
	public void render(GLTexture normalmap, GLTexture heightmap){
		
		shader.bind();
		glBindImageTexture(0, splatmap.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA16F);
		glBindImageTexture(1, heightmap.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(2, normalmap.getHandle(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);
		glFinish();
		splatmap.bind();
		splatmap.bilinearFilter();
	}

}
