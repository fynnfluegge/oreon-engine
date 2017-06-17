#version 330

in vec2 texCoord1;
out vec4 fragColor;

uniform sampler2D texture;
uniform float transparency;

void main()
{
	fragColor = texture2D(texture, texCoord1);
	fragColor.a *= transparency;
}