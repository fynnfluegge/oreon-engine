#version 330

layout (location = 0) in vec3 position0;
layout (location = 2) in vec2 texCoord0;

out vec2 texCoord1;

uniform mat4 orthographicMatrix;

void main()
{
	vec4 vertices[4] = vec4[4](vec4(0.0, 0.0, 0.0, 1.0), vec4(1.0, 0.0, 0.0, 1.0), vec4(0.0, 1.0, 0.0, 1.0), vec4(1.0, 1.0, 0.0, 1.0));
	
	gl_Position = orthographicMatrix * vertices[gl_VertexID];
	texCoord1 = texCoord0;
}