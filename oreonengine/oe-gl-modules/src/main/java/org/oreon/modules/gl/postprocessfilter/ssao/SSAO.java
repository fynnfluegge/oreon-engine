package org.oreon.modules.gl.postprocessfilter.ssao;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;

import java.nio.ByteBuffer;

import org.oreon.core.gl.texture.Texture2D;
import org.oreon.core.math.Vec3f;

public class SSAO {
	
	private static Vec3f[] kernel;
	private Texture2D noiseTexture;
	
	private NoiseTextureShader noiseTextureShader;
	
	public SSAO() {
	
		generateKernel(32);
		
		noiseTextureShader = NoiseTextureShader.getInstance();
		
		noiseTexture = new Texture2D();
		noiseTexture.generate();
		noiseTexture.bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F,
				4,
				4,
				0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
		noiseTexture.noFilter();
	}
	
	public void render(){
		
	}
	
	public void generateKernel(int kernelSize){
		
		kernel = new Vec3f[kernelSize];
		
		for (int i=0; i<kernelSize; i++){
			kernel[i] = new Vec3f((float) Math.random()*2-1,
								  (float) Math.random()*2-1,
								  (float) Math.random());
			kernel[i].normalize();
			
			float scale = (float) i / (float) kernelSize;
			
			scale = (float) Math.min(Math.max(0.1, scale*scale), 1.0);
			
			kernel[i] = kernel[i].mul(scale);
		}
	}

}
