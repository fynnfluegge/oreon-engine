package org.oreon.gl.components.terrain;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.util.List;

import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.Texture2DStorageRGBA32F;

import lombok.Getter;

public class FractalMapGenerator {

	@Getter
	private GLTexture fractalmap;
	
	private FractalMapShader shader;
	private int N;

	public FractalMapGenerator(int N) {
		
		this.N = N;
		shader = FractalMapShader.getInstance();
		fractalmap = new Texture2DStorageRGBA32F(N,N,(int) (Math.log(N)/Math.log(2)));
		fractalmap.bind();
		fractalmap.bilinearFilter();
		fractalmap.unbind();
		fractalmap.getMetaData().setWidth(N);
		fractalmap.getMetaData().setHeight(N);
	}
	
	public void render(List<FractalMap> fractals){
		
		shader.bind();
		shader.updateUniforms(fractals, N);
		glBindImageTexture(0, fractalmap.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);
		glFinish();
		fractalmap.bind();
		fractalmap.bilinearFilter();
	}

}
