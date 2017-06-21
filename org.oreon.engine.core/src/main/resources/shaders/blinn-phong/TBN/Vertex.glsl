#version 330

layout (location = 0) in vec3 position0;
layout (location = 1) in vec3 normal0;
layout (location = 2) in vec2 texCoord0;
layout (location = 3) in vec3 tangent0;
layout (location = 4) in vec3 bitangent0;

out vec3 normal1;
out vec2 texCoord1;
out vec3 tangent1;
out vec3 bitangent1;
out vec3 position1;

uniform mat4 worldMatrix;
uniform mat4 modelMatrix;

void main()
{
	gl_Position = worldMatrix * vec4(position0,1);
	normal1 = normalize(modelMatrix * vec4(normal0,1)).xyz;
	texCoord1 = texCoord0;
	tangent1 = normalize(modelMatrix * vec4(tangent0,1)).xyz;
	bitangent1 = normalize(modelMatrix * vec4(bitangent0,1)).xyz;
	position1 = (worldMatrix * vec4(position0,1)).xyz;
}