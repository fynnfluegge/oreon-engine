#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec2 inUV;

layout (location = 0) out vec4 fragColor;

layout (set = 0, binding = 0) uniform sampler2D fontSampler;

layout (push_constant, std430, row_major) uniform pushConstants{
	mat4 orthographicMatrix;
} constants;

void main()
{
	vec4 rgba = texture(fontSampler, inUV);
	
	fragColor = rgba;
}



