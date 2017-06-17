#version 330

layout (location = 0) in vec3 position0;
layout (location = 1) in vec3 normal0;
layout (location = 2) in vec2 texCoord0;

out vec3 worldPosition;

uniform mat4 modelViewProjectionMatrix;
uniform mat4 worldMatrix;

void main()
{
	gl_Position = modelViewProjectionMatrix * vec4(position0,1);
	worldPosition = (worldMatrix * vec4(position0,1)).xyz;
}




