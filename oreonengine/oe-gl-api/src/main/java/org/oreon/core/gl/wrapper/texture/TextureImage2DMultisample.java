package org.oreon.core.gl.wrapper.texture;

import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;

import org.oreon.core.gl.texture.GLTexture;

public class TextureImage2DMultisample extends GLTexture{

	public TextureImage2DMultisample(int width, int height, int samples,
			ImageFormat imageFormat, SamplerFilter samplerFilter, TextureWrapMode textureWrapMode) {
		
		super(GL_TEXTURE_2D_MULTISAMPLE, width, height);
		
		bind();
		
		switch(imageFormat)
		{
			case RGBA16FLOAT:
				allocateImage2DMultisample(samples, GL_RGBA16F); break;
			case RGBA32FLOAT:
				allocateImage2DMultisample(samples, GL_RGBA32F); break;
			case DEPTH32FLOAT:
				allocateImage2DMultisample(samples, GL_DEPTH_COMPONENT32F); break;
		}
		
		switch(samplerFilter)
		{
			case Nearest:
				nearestFilter(); break;
			case Bilinear:
				bilinearFilter(); break;
			case Trilinear:
				trilinearFilter(); break;
			case Anistropic:
				anisotropicFilter(); break;

		}
		
		switch(textureWrapMode)
		{
			case ClampToBorder:
				clampToBorder(); break;
			case ClampToEdge:
				clampToEdge(); break;
			case MirrorRepeat:
				mirrorRepeat(); break;
			case Repeat:
				repeat(); break;
			case None: break;
		}
		
		unbind();
	}

}
