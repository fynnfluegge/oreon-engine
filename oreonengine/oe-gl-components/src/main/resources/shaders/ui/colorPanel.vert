#version 330

layout (location = 0) in vec3 position0;

uniform mat4 orthographicMatrix;

void main()
{
	gl_Position = orthographicMatrix * vec4(position0, 1);
}