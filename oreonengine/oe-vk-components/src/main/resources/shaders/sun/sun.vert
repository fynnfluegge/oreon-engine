#version 450
#extension GL_ARB_separate_shader_objects : enable
#extension GL_ARB_shading_language_420pack : enable

layout (location = 0) in vec3 position;                                          

layout(binding = 0, set = 0, std140, row_major) uniform Camera {
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};   

layout (push_constant, std430, row_major) uniform pushConstants{
	mat4 m_World;
} constants;

out gl_PerVertex {
	vec4 gl_Position;
	float gl_PointSize;
};
 
void main()                                                                         
{      
    gl_PointSize = 300;
	gl_Position = m_ViewProjection * (constants.m_World * vec4(position,1.0));                                             
}