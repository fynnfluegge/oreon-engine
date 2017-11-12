package org.oreon.modules.gl.postprocessfilter.ssao;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL15.GL_READ_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

import java.nio.ByteBuffer;
import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.gl.texture.Texture2DMultisample;
import org.oreon.core.math.Vec3f;
import org.oreon.core.system.CoreSystem;

public class SSAO {
	
	private int kernelSize;
	private Vec3f[] kernel;
	private float[] randomx;
	private float[] randomy;
	private Texture2D noiseTexture;
	private Texture2D ssaoSceneTexture;
	private Texture2D ssaoBlurSceneTexture;
	
	private NoiseTextureShader noiseTextureShader;
	private SSAOShader ssaoShader;
	private SSAOBlurShader blurShader;
	
	private int width;
	private int height;
	
	public SSAO(int width, int height) {

		this.width = width;
		this.height = height;
		
		kernelSize = 32;
		
		noiseTextureShader = NoiseTextureShader.getInstance();
		ssaoShader = SSAOShader.getInstance();
		blurShader = SSAOBlurShader.getInstance();
		
		randomx = new float[16];
		randomy = new float[16];
		
		for (int i=0; i<16; i++){
			randomx[i] = (float) Math.random() * 2 - 1;
			randomy[i] = (float) Math.random() * 2 - 1;
		}
	
		generateKernel(kernelSize);
		
		noiseTexture = new Texture2D();
		noiseTexture.generate();
		noiseTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F,
				4,
				4,
				0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		noiseTexture.noFilter();
		
		ssaoSceneTexture = new Texture2D();
		ssaoSceneTexture.generate();
		ssaoSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F,
				width,
				height,
				0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		ssaoSceneTexture.noFilter();
		
		ssaoBlurSceneTexture = new Texture2D();
		ssaoBlurSceneTexture.generate();
		ssaoBlurSceneTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F,
				width,
				height,
				0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		ssaoBlurSceneTexture.noFilter();
		
		// generate Noise
		noiseTextureShader.bind();
		glBindImageTexture(0, noiseTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		noiseTextureShader.updateUniforms(randomx, randomy);
		glDispatchCompute(1,1,1);
	}
	
	public void render(Texture2DMultisample worldPositionSceneTexture,
					   Texture2DMultisample normalSceneTexture){
		
		ssaoShader.bind();
		glBindImageTexture(0, ssaoSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		glBindImageTexture(1, worldPositionSceneTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(2, normalSceneTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glBindImageTexture(3, noiseTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		ssaoShader.updateUniforms(CoreSystem.getInstance().getScenegraph().getCamera().getViewMatrix(),
								  CoreSystem.getInstance().getScenegraph().getCamera().getProjectionMatrix(),
								  kernel);
		glDispatchCompute(width/16,height/16,1);
		
		blurShader.bind();
		glBindImageTexture(0, ssaoBlurSceneTexture.getId(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		glBindImageTexture(1, ssaoSceneTexture.getId(), 0, false, 0, GL_READ_ONLY, GL_RGBA32F);
		glDispatchCompute(width/16,height/16,1);
	}
	
	public void generateKernel(int kernelSize){
		
		kernel = new Vec3f[kernelSize];
		
		for (int i=0; i<kernelSize; i++){
			kernel[i] = new Vec3f((float) Math.random()*2-1,
								  (float) Math.random()*2-1,
								  (float) Math.random());
			kernel[i].normalize();
			
			float scale = (float) i / (float) kernelSize;
			
			scale = (float) Math.min(Math.max(0.01, scale*scale), 1.0);
			
			kernel[i] = kernel[i].mul(scale).mul(-1);
		}
	}

	public Texture2D getNoiseTexture() {
		return noiseTexture;
	}

	public void setNoiseTexture(Texture2D noiseTexture) {
		this.noiseTexture = noiseTexture;
	}

	public Texture2D getSsaoSceneTexture() {
		return ssaoSceneTexture;
	}

	public void setSsaoSceneTexture(Texture2D ssaoSceneTexture) {
		this.ssaoSceneTexture = ssaoSceneTexture;
	}

	public Texture2D getSsaoBlurSceneTexture() {
		return ssaoBlurSceneTexture;
	}

	public void setSsaoBlurSceneTexture(Texture2D ssaoBlurSceneTexture) {
		this.ssaoBlurSceneTexture = ssaoBlurSceneTexture;
	}

}
