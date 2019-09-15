#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec2 inUV;
layout (location = 1) in vec4 inViewPos;
layout (location = 2) in vec3 inWorldPos;
layout (location = 3) in vec3 inTangent;

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 worldPosition_out;
layout(location = 2) out vec4 normal_out;
layout(location = 3) out vec4 specular_emission_diffuse_ssao_bloom_out;
layout(location = 4) out vec4 lightScattering_out;

struct Material
{
	sampler2D diffusemap;
	sampler2D normalmap;
	sampler2D heightmap;
	float heightScaling;
	float uvScaling;
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

layout (std140) uniform DirectionalLight{
	vec3 direction;
	float intensity;
	vec3 ambient;
	vec3 color;
} directional_light;

layout (std430, binding = 1) buffer ssbo0 {
	vec3 fogColor;
	float sightRangeFactor;
	int diamond_square_enable;
	int tessFactor;
	float tessSlope;
	float tessShift;
	float xzScale;
	int isBezier;
	float uvScale;
	int largeDetailRange;
};

uniform sampler2D normalmap;
uniform sampler2D splatmap;
uniform Material materials[3];
uniform int isReflection;
uniform int isRefraction;
uniform int isCameraUnderWater;
uniform vec4 clipplane;
uniform sampler2D dudvCaustics;
uniform sampler2D caustics;
uniform float distortionCaustics;
uniform float underwaterBlurFactor;

const float zfar = 10000;
const float znear = 0.1;

float diffuse(vec3 direction, vec3 normal, float intensity)
{
	return max(0.0, dot(normal, -direction) * intensity);
}

float getFogFactor(float dist)
{
	return clamp(smoothstep(0.0,1.0,-0.0002/sightRangeFactor*(dist-(zfar)/10*sightRangeFactor) + 1),0.0,1.0);
}

float distancePointPlane(vec3 point, vec4 plane){
	return abs(plane.x*point.x + plane.y*point.y + plane.z*point.z + plane.w) / 
		   abs(sqrt(plane.x * plane.x + plane.y * plane.y + plane.z * plane.z));
}

void main()
{		
	float dist = length(eyePosition - inWorldPos);
	float height = inWorldPos.y;
	
	// normalmap/occlusionmap/splatmap coords
	vec2 mapCoords = (inWorldPos.xz + xzScale/2)/xzScale; 
	vec3 normal = texture(normalmap, mapCoords).rgb;
	normal = normalize(normal);
	
	vec4 v_splatmap = texture(splatmap, mapCoords).rgba;
	float[4] blendValues = float[](v_splatmap.r,v_splatmap.g,v_splatmap.b,v_splatmap.a);
	
	if (dist < largeDetailRange-50)
	{
		float attenuation = clamp(-dist/(largeDetailRange-50) + 1,0.0,1.0);
		
		vec3 bitangent = normalize(cross(normal, inTangent));
		mat3 TBN = mat3(inTangent,bitangent,normal);
		
		vec3 bumpNormal;
		
		for (int i=0; i<3; i++){
			bumpNormal += (2*(texture(materials[i].normalmap, inUV/materials[i].uvScaling).rgb) - 1) * blendValues[i];
		}
		
		bumpNormal = normalize(bumpNormal);
		bumpNormal.xy *= attenuation;
		normal = normalize(TBN * bumpNormal);
	}
	
	vec3 fragColor = vec3(0,0,0);
	
	for (int i=0; i<3; i++){
		fragColor +=  texture(materials[i].diffusemap, inUV/materials[i].uvScaling).rgb * blendValues[i];
	}
	
	// caustics
	if (isCameraUnderWater == 1 && isRefraction == 0){
		vec2 causticsUV = inWorldPos.xz / 400;
		vec2 causticDistortion = texture(dudvCaustics, causticsUV*0.2 + distortionCaustics).rb * 0.18;
		vec3 causticsColor = texture(caustics, causticsUV + causticDistortion).rbg;
		
		fragColor += (causticsColor/4);
	}
	
	if (isReflection == 1 || isRefraction == 1){
		float diff = diffuse(directional_light.direction, normal.xzy, directional_light.intensity);
		vec3 diffuseLight = directional_light.ambient + directional_light.color * diff;
		fragColor *= diffuseLight;
	}

	// underwater blur
	if (isCameraUnderWater == 0 && isRefraction == 1){
		float distToWaterSurace = distancePointPlane(inWorldPos,clipplane);
		float refractionFactor = smoothstep(0,1,1-1/(underwaterBlurFactor*distToWaterSurace+1) + 0.1);
		fragColor = mix(fragColor, vec3(0), refractionFactor); 
	}
	
	if (isReflection == 1){
		float dist = length(eyePosition - inWorldPos);
		float fogFactor = getFogFactor(dist);
		fragColor = mix(fogColor.rgb * 2, fragColor, fogFactor);
	}
	
	albedo_out = vec4(fragColor,1);
	worldPosition_out = vec4(inWorldPos,1);
	normal_out = vec4(normal,1);
	specular_emission_diffuse_ssao_bloom_out = vec4(1,0.0,11,1);
	lightScattering_out = vec4(0,0,0,1);
}