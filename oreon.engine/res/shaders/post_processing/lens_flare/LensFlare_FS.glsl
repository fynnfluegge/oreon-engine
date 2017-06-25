#version 330

layout(location = 0) out vec4 outputColor;

in vec2 texCoord1;

uniform sampler2D texture;
uniform float transparency;

void main()
{
	outputColor = texture2D(texture, texCoord1);
	outputColor.a = 1;
}