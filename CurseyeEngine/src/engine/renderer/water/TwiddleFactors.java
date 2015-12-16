package engine.renderer.water;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;
import engine.core.Texture;
import engine.renderpipeline.data.SSBO;
import engine.renderpipeline.shaderPrograms.ocean.TwiddleFactorsShader;

public class TwiddleFactors {

	private int N;
	private int log_2_N;
	private TwiddleFactorsShader shader;
	private SSBO bitReversedSSBO;
	private Texture texture;
	
	public TwiddleFactors(int N)
	{
		this.N = N;
		
		bitReversedSSBO = new SSBO();
		bitReversedSSBO.addData(initBitReversedIndices());
		bitReversedSSBO.bind(0);
		
		log_2_N = (int) (Math.log(N)/Math.log(2));
		shader = TwiddleFactorsShader.getInstance();
		texture = new Texture();
		texture.generate();
		texture.bind();
		texture.noFilter();
		glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, log_2_N, N);
	}
	
	public void renderToTexture()
	{
		shader.execute();
		shader.sendUniforms(N);
		glBindImageTexture(0, texture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		glDispatchCompute(log_2_N,N/16,1);	
	}
	
	private int[] initBitReversedIndices()
	{
		int[] bitReversedIndices = new int[N];
		int bits = (int) (Math.log(N)/Math.log(2));
		
		for (int i = 0; i<N; i++)
		{
			int x = Integer.reverse(i);
			x = Integer.rotateLeft(x, bits);
			bitReversedIndices[i] = x;
		}
		
		return bitReversedIndices;
	}

	public Texture getTexture() {
		return texture;
	}
}
