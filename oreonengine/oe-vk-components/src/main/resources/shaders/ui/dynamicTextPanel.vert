#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec2 inPosition;

layout (location = 0) out vec2 outUV;

layout (push_constant, std430, row_major) uniform pushConstants{
	mat4 orthographicMatrix;
} constants;

layout(set = 0, binding = 1) uniform UniformBufferObject {
	vec2[64] uv;
} ubo;

out gl_PerVertex {
	vec4 gl_Position;	
};

void main()
{
	gl_Position = constants.orthographicMatrix * vec4(inPosition, 0, 1);
	outUV = ubo.uv[gl_VertexIndex];
}