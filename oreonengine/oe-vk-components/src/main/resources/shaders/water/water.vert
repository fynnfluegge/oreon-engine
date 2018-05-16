#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec2 inPosition;

layout (location = 0) out vec2 outUV;

layout (push_constant, std430, row_major) uniform Constants{
	mat4 m_World;
	vec2 windDirection;
	float tessSlope;
	float tessShift;
	int tessFactor;
	int uvScale;
	float displacementScale;
	float choppiness;
	int highDetailRange;
	float kReflection;
	float kRefraction;
	int windowWidth;
	int windowHeight;
	float emission;
	float specular;
} constants;

out gl_PerVertex {
	vec4 gl_Position;	
};

void main()
{
		gl_Position = constants.m_World * vec4(inPosition.x,0,inPosition.y,1);
		outUV = inPosition.xy;
}
