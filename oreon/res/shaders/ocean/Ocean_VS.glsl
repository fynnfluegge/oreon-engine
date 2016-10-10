#version 430

layout (location = 0) in vec2 position0;

uniform mat4 worldMatrix;

out vec2 texCoord1;

void main()
{
		gl_Position = worldMatrix * vec4(position0.x,0,position0.y,1);
		texCoord1 = position0.xy;
}