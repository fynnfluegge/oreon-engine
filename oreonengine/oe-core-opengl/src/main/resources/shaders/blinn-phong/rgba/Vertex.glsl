#version 330

layout (location = 0) in vec3 position0;
layout (location = 1) in vec3 normal0;
layout (location = 2) in vec2 texCoord0;

out vec3 position1;
out vec3 normal1;
out vec2 texCoord1;

uniform mat4 modelViewProjectionMatrix;
uniform mat4 worldMatrix;
uniform vec4 clipplane;

void main()
{
	gl_Position = modelViewProjectionMatrix * vec4(position0, 1);
	gl_ClipDistance[0] = dot(worldMatrix * vec4(position0,1), clipplane);
	
	position1 = (worldMatrix * vec4(position0,1)).xyz;
	normal1 = normalize(vec4(normal0,1)).xyz;
	texCoord1 = texCoord0;
}




