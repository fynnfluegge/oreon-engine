package org.oreon.core.image;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ImageMetaData {

	int width;
	int height;
	int channels;
	
	public ImageMetaData(){
		
	}
}
