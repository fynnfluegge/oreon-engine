#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec3 position0;

layout (location = 0) out vec3 worldPosition;

layout(binding = 0, set = 0, std140, row_major) uniform Camera {
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

layout (push_constant) uniform Constants{
	mat4 m_World;
};

out gl_PerVertex {
	vec4 gl_Position;	
};

void main()
{
	gl_Position = m_ViewProjection * m_World * vec4(position0,1);
	worldPosition = (m_World * vec4(position0,1)).xyz;
}




