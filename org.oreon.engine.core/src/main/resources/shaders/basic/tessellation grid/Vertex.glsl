#version 330

layout (location = 0) in vec3 position0;

out vec2 texCoord1;

uniform mat4 worldMatrix;

void main()
{
	gl_Position = worldMatrix * vec4(position0.x,0,position0.y,1);
	texCoord1 = position0.xy;
}