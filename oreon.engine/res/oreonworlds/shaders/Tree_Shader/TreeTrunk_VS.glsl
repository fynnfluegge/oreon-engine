#version 430

layout (location = 0) in vec3 position0;
layout (location = 1) in vec3 normal0;
layout (location = 2) in vec2 texCoord0;
layout (location = 3) in vec3 tangent0;
layout (location = 4) in vec3 bitangent0;

out vec4 normal_GS;
out vec2 texCoord_GS;
out int instanceID_GS;
out vec4 tangent_GS;
out vec4 bitangent_GS;

void main()
{
	gl_Position = vec4(position0,1);
	normal_GS = vec4(normal0,1);
	texCoord_GS = texCoord0;
	instanceID_GS = gl_InstanceID;
	tangent_GS = vec4(tangent0,1);
	bitangent_GS = vec4(bitangent0,1);
}