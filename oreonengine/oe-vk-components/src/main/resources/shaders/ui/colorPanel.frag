#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) out vec4 outColor;

layout (push_constant, std430, row_major) uniform pushConstants{
	mat4 orthographicMatrix;
	vec4 rgba;
} constants;

void main() {

    outColor = constants.rgba;
}