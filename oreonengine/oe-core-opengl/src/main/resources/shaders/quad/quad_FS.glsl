#version 330

in vec2 texCoord1;

layout(location = 0) out vec4 fragColor;

uniform sampler2D texture;

void main()
{
	vec4 rgba = texture2D(texture, texCoord1);
	
	if (rgba.a == 0)
		discard;
		
	fragColor = rgba;
}


