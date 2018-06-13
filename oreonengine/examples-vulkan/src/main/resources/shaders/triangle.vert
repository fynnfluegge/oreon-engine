#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec2 inUV;

layout(location = 0) out vec2 uv;

layout(binding = 0, set = 0, std140, row_major) uniform Camera {
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

out gl_PerVertex {
	vec4 gl_Position;	
};

void main()
{
	gl_Position = m_ViewProjection * vec4(inPosition,1.0);
	uv = inUV;
}