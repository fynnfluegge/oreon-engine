#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) flat in int inVertexId;

layout (location = 0) out vec4 fragColor;

layout (set = 0, binding = 0) uniform sampler2D fontSampler;

layout (push_constant, std430, row_major) uniform pushConstants{
	mat4 orthographicMatrix;
	vec2[128] uv;
} constants;

void main()
{
	vec4 rgba = texture(fontSampler, constants.uv[inVertexId]);
	
	if (rgba.a < 1.0){
		discard;
	}
	else{
		fragColor = rgba;
	}
}



