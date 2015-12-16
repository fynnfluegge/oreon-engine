#version 430

layout (location = 0) in vec3 position0;
layout (location = 1) in vec2 texCoord0;

uniform mat4 worldMatrix;

out vec2 texCoord1;

void main()
{
		gl_Position = worldMatrix * vec4(position0,1);
		texCoord1 = texCoord0;
}