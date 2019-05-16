package org.oreon.core.image;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Image {

	protected ImageMetaData metaData;
	
	public enum ImageFormat {

		RGBA32FLOAT,
		RGBA16FLOAT,
		DEPTH32FLOAT
	}
	
	public enum SamplerFilter {

		Nearest,
		Bilinear,
		Trilinear,
		Anistropic
	}
	
	public enum TextureWrapMode {
		
		None,
		ClampToEdge,
		ClampToBorder,
		Repeat,
		MirrorRepeat
		
	}
		
}
