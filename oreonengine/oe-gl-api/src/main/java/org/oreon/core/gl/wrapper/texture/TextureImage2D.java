package org.oreon.core.gl.wrapper.texture;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;

import org.oreon.core.gl.texture.GLTexture;

public class TextureImage2D extends GLTexture{

	public TextureImage2D(int width, int height,
			ImageFormat imageFormat, SamplerFilter samplerFilter, TextureWrapMode textureWrapMode) {
		
		super(GL_TEXTURE_2D, width, height);
		
		bind();
		switch(imageFormat)
		{
			case RGBA16FLOAT:
				allocateImage2D(GL_RGBA16F, GL_RGBA, GL_FLOAT); break;
			case RGBA32FLOAT:
				allocateImage2D(GL_RGBA16F, GL_RGBA, GL_FLOAT); break;
			case DEPTH32FLOAT:
				allocateImage2D(GL_RGBA32F, GL_RGBA, GL_FLOAT); break;
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
	
	public TextureImage2D(String file, SamplerFilter samplerFilter,
			TextureWrapMode textureWrapMode) {
		
		super(file);
		
		bind();
		
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
