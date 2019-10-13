#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec2 inPosition;

layout (location = 0) out vec2 outUV;

layout (push_constant, std430, row_major) uniform Constants{
	mat4 worldMatrix;
	int uvScale;
	int tessFactor;
	float tessSlope;
	float tessShift;
	float displacementScale;
	int highDetailRange;
	float choppiness;
	float kReflection;
	float kRefraction;
	int windowWidth;
	int windowHeight;
	int diffuseEnable;
	float emission;
	float specularFactor;
	float specularAmplifier;
	float reflectionBlendFactor;
	vec3 waterColor;
	float fresnelFactor;
	float capillarStrength;
	float capillarDownsampling;
	float dudvDownsampling;
	vec2 windDirection;
} constants;

out gl_PerVertex {
	vec4 gl_Position;	
};

void main()
{
		gl_Position = constants.worldMatrix * vec4(inPosition.x,0,inPosition.y,1);
		outUV = inPosition.xy;
}
