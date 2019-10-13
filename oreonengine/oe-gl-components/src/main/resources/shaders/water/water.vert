#version 430
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec2 position;

layout (location = 0) out vec2 outUV;

layout (std430, row_major, binding = 1) buffer ssbo {
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
};

void main()
{
		gl_Position = worldMatrix * vec4(position.x,0,position.y,1);
		outUV = position.xy;
}
