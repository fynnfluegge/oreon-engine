#version 330

in vec3 position_FS;
in vec2 texCoord_FS;
in vec3 normal_FS;

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

const float zFar = 10000;
const float zNear = 0.1;

float alphaDistanceFactor(float dist)
{
	return clamp(0.003f * (-dist+750),0,1);
}

void main()
{	
	float dist = length(eyePosition - position_FS);
	
	vec3 albedo = texture(material.diffusemap, texCoord_FS).rgb;
	
	float alpha = texture(material.diffusemap, texCoord_FS).a;
	
	if (alpha < 0.8)
		discard;
	
	alpha *= alphaDistanceFactor(dist);

	albedo_out = vec4(albedo,1);
	worldPosition_out = vec4(position_FS,1);
	normal_out = vec4(normal_FS.xzy,1);
	specularEmission_out = vec4(1,0,0,1);
	lightScattering_out = vec4(0,0,0,1);
}