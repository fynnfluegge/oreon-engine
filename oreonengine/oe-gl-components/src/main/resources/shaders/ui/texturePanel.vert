#version 330

layout (location = 0) in vec3 inPosition;
layout (location = 1) in vec2 texCoord0;

out vec2 texCoord1;

uniform mat4 orthographicMatrix;

void main()
{
	gl_Position = orthographicMatrix * vec4(inPosition, 1);
	texCoord1 = vec2(inPosition.x,1-inPosition.y);
}