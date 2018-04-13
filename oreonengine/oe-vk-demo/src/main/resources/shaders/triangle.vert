#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec2 inUV;

layout(location = 0) out vec2 uv;

layout(binding = 0, set = 0, std140, row_major) uniform UniformBufferObject {
	mat4 m_viewProj;
} ubo;

out gl_PerVertex {
	vec4 gl_Position;	
};

void main()
{
	gl_Position = ubo.m_viewProj * vec4(inPosition,1.0);
	uv = inUV;
}