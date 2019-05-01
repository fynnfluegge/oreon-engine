#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec3 inPosition;

uniform mat4 m_MVP;

void main()
{
	gl_Position = m_MVP * vec4(inPosition,1);
}