#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) out vec4 fragColor;

void main()
{
	fragColor = vec4(0.07,0.08,0.9,1.0);
}