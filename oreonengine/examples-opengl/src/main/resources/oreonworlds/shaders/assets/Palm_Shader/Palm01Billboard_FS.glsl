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

float alphaDistanceFactor(float dist)
{
	return 0.04f * (dist-250);
}

void main()
{	
	float dist = length(eyePosition - position_FS);
	
	vec3 fragColor = texture(material.diffusemap, texCoord_FS).rgb;
	
	float alpha = texture(material.diffusemap, texCoord_FS).a;
	alpha *= alphaDistanceFactor(dist);
	
	albedo_out = vec4(fragColor,alpha);
	worldPosition_out = vec4(position_FS,1);
	normal_out = vec4(0,0,1,1);
	specularEmission_out = vec4(1,0,0,1);
	lightScattering_out = vec4(0,0,0,1);
}