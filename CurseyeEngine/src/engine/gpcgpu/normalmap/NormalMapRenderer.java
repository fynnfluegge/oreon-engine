package engine.gpcgpu.normalmap;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;
import engine.core.Texture;
import engine.shaders.gpcgpu.NormalMapShader;

public class NormalMapRenderer {

	private float strength;
	private Texture normalmap;
	private NormalMapShader shader;
	private int N;
	
	public NormalMapRenderer(int N){
		this.N = N;
		shader = NormalMapShader.getInstance();
		normalmap = new Texture();
		normalmap.generate();
		normalmap.bind();
		glTexStorage2D(GL_TEXTURE_2D,  (int) (Math.log(N)/Math.log(2)), GL_RGBA32F, N, N);
	}
	
	public void render(Texture heightmap){
		shader.execute();
		shader.sendUniforms(heightmap, N, strength);
		glBindImageTexture(0, normalmap.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		glDispatchCompute(N/16,N/16,1);
		glFinish();
		normalmap.bind();
		normalmap.mipmap();
	}

	public float getStrength() {
		return strength;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}

	public Texture getNormalmap() {
		return normalmap;
	}

	public void setNormalmap(Texture normalmap) {
		this.normalmap = normalmap;
	}
}
