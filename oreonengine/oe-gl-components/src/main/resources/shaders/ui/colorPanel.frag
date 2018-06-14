#version 330

layout(location = 0) out vec4 fragColor;

uniform vec4 rgba;

void main()
{
	fragColor = rgba;
}



