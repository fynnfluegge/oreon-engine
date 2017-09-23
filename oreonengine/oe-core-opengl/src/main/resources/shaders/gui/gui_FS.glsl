#version 330

in vec2 texCoord1;

out vec4 fragColor;

uniform sampler2D texture;

void main()
{
	vec4 rgba = texture2D(texture, texCoord1);
	
	if (rgba.a < 1.0){
		discard;
	}
	else{
		fragColor = texture2D(texture, texCoord1);
	}
}



