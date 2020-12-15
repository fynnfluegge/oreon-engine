#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec3 inPosition;

layout (location = 0) out vec3 modelPosition;

uniform mat4 modelViewProjectionMatrix;
uniform mat4 modelMatrix;
void main()
{
	gl_Position = modelViewProjectionMatrix * vec4(inPosition,1);

	modelPosition = (modelMatrix * vec4(inPosition,1)).xyz;
}




