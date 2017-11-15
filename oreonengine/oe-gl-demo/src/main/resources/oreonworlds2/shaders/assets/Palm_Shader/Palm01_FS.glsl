#version 430

in vec3 normal_FS;
in vec3 position_FS;

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 worldPosition_out;
layout(location = 2) out vec4 normal_out;
layout(location = 3) out vec4 specularEmission_out;
layout(location = 4) out vec4 lightScattering_out;

struct Material
{
	vec3 color;
	float shininess;
	float emission;
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 viewProjectionMatrix;
	vec4 frustumPlanes[6];
};

uniform Material material;
uniform float sightRangeFactor;

const float zFar = 10000;
const float zNear = 0.1;
const vec3 fogColor = vec3(0.62,0.85,0.95);

float alphaDistanceFactor(float dist)
{
	return clamp(0.004f * (-dist+500),0,1);
}

void main()
{	
	float dist = length(eyePosition - position_FS);

	vec3 fragColor = material.color;
	
	float fogFactor = -0.0005/sightRangeFactor*(dist-zFar/5*sightRangeFactor);
	
    vec3 rgb = mix(fogColor, fragColor, clamp(fogFactor,0,1));
	
	float alpha = 1;
	alpha *= alphaDistanceFactor(dist);
		
	albedo_out = vec4(rgb,1);
	worldPosition_out = vec4(position_FS,1);
	normal_out = vec4(normal_FS,1);
	specularEmission_out = vec4(1,0,0,1);
	lightScattering_out = vec4(0,0,0,1);
}