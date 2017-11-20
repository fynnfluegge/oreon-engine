#version 430

in vec2 texCoord_FS;
in vec3 position_FS;
in vec3 normal_FS;
in vec3 tangent_FS;
in vec3 bitangent_FS;

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 worldPosition_out;
layout(location = 2) out vec4 normal_out;
layout(location = 3) out vec4 specularEmission_out;
layout(location = 4) out vec4 lightScattering_out;

struct Material
{
	sampler2D diffusemap;
	sampler2D normalmap;
	float shininess;
	float emission;
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 viewProjectionMatrix;
	vec4 frustumPlanes[6];
};

uniform vec4 clipplane;
uniform Material material;
uniform int isReflection;
uniform int isRefraction;
uniform int isCameraUnderWater;
uniform sampler2D dudvCaustics;
uniform sampler2D caustics;
uniform float distortionCaustics;

const vec3 waterRefractionColor = vec3(0.1,0.125,0.19);

float distancePointPlane(vec3 point, vec4 plane){
	return abs(plane.x*point.x + plane.y*point.y + plane.z*point.z + plane.w) / 
		   abs(sqrt(plane.x * plane.x + plane.y * plane.y + plane.z * plane.z));
}

void main()
{	
	float dist = length(eyePosition - position_FS);

	mat3 TBN = mat3(tangent_FS, normal_FS, bitangent_FS);
	vec3 normal = normalize(2*(texture(material.normalmap, texCoord_FS*4).rbg)-1);
	normal = normalize(TBN * normal);
	
	vec3 albedo = texture(material.diffusemap, texCoord_FS*4).rgb;
	
	// caustics
	if (isCameraUnderWater == 1 && isRefraction == 0){
		vec2 causticsTexCoord = position_FS.xz / 80;
		vec2 causticDistortion = texture(dudvCaustics, causticsTexCoord*0.2 + distortionCaustics*0.6).rb * 0.18;
		vec3 causticsColor = texture(caustics, causticsTexCoord + causticDistortion).rbg;
		
		fragColor += (causticsColor/4);
	}	
	
	// underwater distance blue blur
	if (isRefraction == 1 && isCameraUnderWater == 0){
		
		float distToWaterSurace = distancePointPlane(position_FS,clipplane);
		float refractionFactor = clamp(0.02 * distToWaterSurace,0,1);
		
		rgb = mix(rgb, waterRefractionColor, refractionFactor); 
	}
	
	albedo_out = vec4(albedo,1);
	worldPosition_out = vec4(position_FS,1);
	normal_out = vec4(normal_FS.xzy,1);
	specularEmission_out = vec4(1,0,0,1);
	lightScattering_out = vec4(0,0,0,1);
}