package org.oreon.gl.components.terrain.fractals;

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
	private GLTexture heightmap;
	
	@Getter
	private GLTexture normalmap;
	
	private FractalMapShader shader;
	private int N;

	public FractalMapGenerator(int N) {
		
		this.N = N;
		shader = FractalMapShader.getInstance();
		
		heightmap = new Texture2DStorageRGBA32F(N,N,(int) (Math.log(N)/Math.log(2)));
		heightmap.bind();
		heightmap.bilinearFilter();
		heightmap.unbind();
		heightmap.getMetaData().setWidth(N);
		heightmap.getMetaData().setHeight(N);
		
		normalmap = new Texture2DStorageRGBA32F(N,N,(int) (Math.log(N)/Math.log(2)));
		normalmap.bind();
		normalmap.bilinearFilter();
		normalmap.unbind();
		normalmap.getMetaData().setWidth(N);
		normalmap.getMetaData().setHeight(N);
	}
	
	public void renderHeightmap(List<FractalMap> fractals){
		
		shader.bind();
		shader.updateUniforms(fractals, N, true);
		glBindImageTexture(0, heightmap.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);
		glFinish();
		heightmap.bind();
		heightmap.bilinearFilter();
	}
	
	public void renderNormalmap(List<FractalMap> fractals){
		
		shader.bind();
		shader.updateUniforms(fractals, N, false);
		glBindImageTexture(0, normalmap.getHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);
		glFinish();
		normalmap.bind();
		normalmap.bilinearFilter();
	}

}
