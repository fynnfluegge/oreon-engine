#version 330

layout (location = 0) in vec3 position0;

uniform mat4 modelViewProjectionMatrix;

void main()
{
	gl_Position = modelViewProjectionMatrix * vec4(position0, 1);
}




