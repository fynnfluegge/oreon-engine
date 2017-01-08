package modules.gpgpu;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import engine.shader.Shader;
import engine.shader.computing.NormalMapShader;
import engine.textures.Texture2D;

public class NormalMapRenderer {

	private float strength;
	private Texture2D normalmap;
	private Shader computeShader;
	private int N;
	
	public NormalMapRenderer(int N){
		this.N = N;
		computeShader = NormalMapShader.getInstance();
		normalmap = new Texture2D();
		normalmap.generate();
		normalmap.bind();
		normalmap.trilinearFilter();
		glTexStorage2D(GL_TEXTURE_2D, (int) (Math.log(N)/Math.log(2)), GL_RGBA32F, N, N);
	}
	
	public void render(Texture2D heightmap){
		computeShader.bind();
		computeShader.updateUniforms(heightmap, N, strength);
		glBindImageTexture(0, normalmap.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);
		glFinish();
		normalmap.bind();
		normalmap.trilinearFilter();
	}

	public float getStrength() {
		return strength;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}

	public Texture2D getNormalmap() {
		return normalmap;
	}

	public void setNormalmap(Texture2D normalmap) {
		this.normalmap = normalmap;
	}
}
