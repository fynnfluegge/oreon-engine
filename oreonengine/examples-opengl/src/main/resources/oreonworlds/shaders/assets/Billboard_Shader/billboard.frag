#version 430

in vec3 position_FS;
in vec2 texCoord_FS;

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 worldPosition_out;
layout(location = 2) out vec4 normal_out;
layout(location = 3) out vec4 specularEmission_out;
layout(location = 4) out vec4 lightScattering_out;

struct Material
{
	sampler2D diffusemap;
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 viewProjectionMatrix;
	vec4 frustumPlanes[6];
};
	
uniform Material material;
uniform int isReflection;
uniform int isRefraction;
uniform int range = 700;
uniform float alphaDiscardThreshold = 0.5;

float alphaDistanceFactor(float dist)
{
	return 0.01f * (dist-range);
}

void main()
{	
	float dist = length(eyePosition - position_FS);
	
	vec4 albedo = texture(material.diffusemap, texCoord_FS).rgba;
	
	float alpha = albedo.a;
	
	if (alpha < alphaDiscardThreshold)
		discard;
	
	if (isReflection == 0 && isRefraction == 0){
		alpha *= alphaDistanceFactor(dist);
	}

	albedo_out = vec4(albedo.rgb,alpha);
	worldPosition_out = vec4(position_FS,1);
	normal_out = vec4(0,0,1,1);
	specularEmission_out = vec4(1,0,0,1);
	lightScattering_out = vec4(0,0,0,1);
}