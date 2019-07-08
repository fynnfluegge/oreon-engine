#version 430

in vec2 texCoord_FS;
in vec3 position_FS;
in vec3 normal_FS;

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 worldPosition_out;
layout(location = 2) out vec4 normal_out;
layout(location = 3) out vec4 specularEmission_out;
layout(location = 4) out vec4 lightScattering_out;

struct Material
{
	sampler2D diffusemap;
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
uniform int screenWidth;
uniform int screenHeight;
uniform int range = 800;
uniform float alphaDiscardThreshold = 0.5;

float alphaDistanceFactor(float dist)
{
	return clamp(0.01f * (-dist+range),0,1);
}

bool getStippledAlphaLodFactor(vec3 position, float stippleFactor){
	
	vec4 screenSpacePosition = viewProjectionMatrix * vec4(position,1.0);
	screenSpacePosition.xy /= screenSpacePosition.w;
	screenSpacePosition.xy = screenSpacePosition.xy * 0.5 + 0.5;
	screenSpacePosition.xy *= vec2(screenWidth,screenHeight);
	vec2 fracPosition = screenSpacePosition.xy; //vec2(fract(screenSpacePosition.xy * stippleFactor));
	
	return ((step(fracPosition.x,stippleFactor) == 0.0) && (step(fracPosition.y,stippleFactor) == 0.0));
}

void main()
{	
	float dist = length(eyePosition - position_FS);
	
	// discard(getStippledAlphaLodFactor(position_FS, 0.4));
	
	vec4 albedo = texture(material.diffusemap, texCoord_FS).rgba;
	
	float alpha = albedo.a;
	
	if (alpha < alphaDiscardThreshold)
		discard;
		
	// alpha *= alphaDistanceFactor(dist);
	
	albedo_out = vec4(albedo.rgb,1);
	worldPosition_out = vec4(position_FS,1);
	normal_out = vec4(0,0,1,1);
	specularEmission_out = vec4(1,0.0,11,1);
	lightScattering_out = vec4(0,0,0,1);
}