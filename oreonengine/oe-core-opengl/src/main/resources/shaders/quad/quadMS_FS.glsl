#version 430

layout(location = 0) out vec4 fragColor;

in vec2 texCoord1;

uniform sampler2DMS texture;
uniform int width;
uniform int height;
uniform int multisamples;

void main()
{
	float x = texCoord1.x * width;
	float y = texCoord1.y * height;
	ivec2 coord = ivec2(int(x), int(y));
	
	vec4 rgba;
	for (int i=0; i<multisamples; i++){
		rgba += texelFetch(texture, coord,i).rgba; 
	}
	rgba /= 8.0;
	
	fragColor = rgba;
}