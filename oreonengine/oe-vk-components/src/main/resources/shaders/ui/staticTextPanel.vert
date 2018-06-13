#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec2 inPosition;
layout (location = 1) in vec2 inUV;

layout (location = 0) out vec2 outUV;

layout (push_constant, std430, row_major) uniform pushConstants{
	mat4 orthographicMatrix;
} constants;

out gl_PerVertex {
	vec4 gl_Position;	
};

void main()
{
	gl_Position = constants.orthographicMatrix * vec4(inPosition, 0, 1);
	outUV = inUV;
}