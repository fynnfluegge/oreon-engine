#version 430

layout (location = 0) in vec3 position0;
layout (location = 2) in vec2 texCoord0;

out vec2 texCoord1;

void main()
{
	gl_Position = vec4(position0,1.0);
	texCoord1 = texCoord0;
}