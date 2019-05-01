#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec3 inPosition;

layout (location = 0) out vec3 worldPosition;

uniform mat4 modelViewProjectionMatrix;
uniform mat4 worldMatrix;

void main()
{
	gl_Position = modelViewProjectionMatrix * vec4(inPosition,1);
	worldPosition = (worldMatrix * vec4(inPosition,1)).xyz;
}




