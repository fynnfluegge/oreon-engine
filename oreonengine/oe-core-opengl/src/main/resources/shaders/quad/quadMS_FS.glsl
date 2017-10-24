#version 430

layout(location = 0) out vec4 fragColor;

in vec2 texCoord1;

uniform sampler2DMS texture;

void main()
{
	float x = texCoord1.x * 1280.0;
	float y = texCoord1.y * 720.0;
	ivec2 coord = ivec2(int(x), int(y));
	vec4 rgba0 = texelFetch(texture, coord, 0);
	vec4 rgba1 = texelFetch(texture, coord, 1);
	vec4 rgba2 = texelFetch(texture, coord, 2);
	vec4 rgba3 = texelFetch(texture, coord, 3);
	vec4 rgba4 = texelFetch(texture, coord, 4);
	vec4 rgba5 = texelFetch(texture, coord, 5);
	vec4 rgba6 = texelFetch(texture, coord, 6);
	vec4 rgba7 = texelFetch(texture, coord, 7);
	
	vec4 color = rgba0 + rgba1 + rgba2 + rgba3 + rgba4 + rgba5+ rgba6 + rgba7;
	color /= 8;
	
	if (color.a < 1.0){
		discard;
	}
	else{
		fragColor = color;
	}
}