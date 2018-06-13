#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) in vec2 inPosition;

layout (push_constant, std430, row_major) uniform pushConstants{
	mat4 orthographicMatrix;
	vec4 rgba;
} constants;

out gl_PerVertex {
	vec4 gl_Position;	
};

void main()
{
	gl_Position = constants.orthographicMatrix * vec4(inPosition,0.0,1.0);
}