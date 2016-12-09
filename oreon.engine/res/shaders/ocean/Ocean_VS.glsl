#version 430

layout (location = 0) in vec2 position;

uniform mat4 worldMatrix;

out vec2 texCoord_TC;

void main()
{
		gl_Position = worldMatrix * vec4(position.x,0,position.y,1);
		texCoord_TC = position.xy;
}
