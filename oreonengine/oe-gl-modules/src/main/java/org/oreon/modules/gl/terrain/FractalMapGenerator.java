package org.oreon.modules.gl.terrain;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.util.List;

import org.oreon.core.gl.texture.Texture2D;
import org.oreon.modules.gl.terrain.fractals.FractalMap;

public class FractalMapGenerator {

	private Texture2D fractalmap;
	private FractalMapShader shader;
	private int N;

	public FractalMapGenerator(int N) {
		
		this.N = N;
		shader = FractalMapShader.getInstance();
		fractalmap = new Texture2D();
		fractalmap.generate();
		fractalmap.bind();
		fractalmap.bilinearFilter();
		glTexStorage2D(GL_TEXTURE_2D, (int) (Math.log(N)/Math.log(2)), GL_RGBA32F, N, N);
		fractalmap.setWidth(N);
		fractalmap.setHeight(N);
	}
	
	public void render(List<FractalMap> fractals){
		
		shader.bind();
		shader.updateUniforms(fractals, N);
		glBindImageTexture(0, fractalmap.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);
		glFinish();
		fractalmap.bind();
		fractalmap.bilinearFilter();
	}

	public Texture2D getFractalmap() {
		return fractalmap;
	}

}
