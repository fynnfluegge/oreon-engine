#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec2 inUV;
layout (location = 1) in vec4 inViewPos;
layout (location = 2) in vec3 inWorldPos;
layout (location = 3) in vec3 inTangent;

layout(location = 0) out vec4 outAlbedo;
layout(location = 1) out vec4 outWorldPos;
layout(location = 2) out vec4 outNormal;
layout(location = 3) out vec4 outSpecularEmission;
layout(location = 4) out vec4 outLightScattering;

struct Material
{
	sampler2D diffusemap;
	sampler2D normalmap;
	sampler2D heightmap;
	float heightScaling;
	float horizontalScaling;
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

uniform sampler2D normalmap;
uniform sampler2D splatmap;
uniform float scaleY;
uniform float scaleXZ;
uniform Material materials[3];
uniform float sightRangeFactor;
uniform int largeDetailRange;
uniform int isReflection;
uniform int isRefraction;
uniform int isCameraUnderWater;
uniform vec4 clipplane;
uniform sampler2D dudvCaustics;
uniform sampler2D caustics;
uniform float distortionCaustics;

const float zfar = 10000;
const float znear = 0.1;
const vec3 fogColor = vec3(0.65,0.85,0.9);
const vec3 waterRefractionColor = vec3(0.1,0.125,0.19);

float distancePointPlane(vec3 point, vec4 plane){
	return abs(plane.x*point.x + plane.y*point.y + plane.z*point.z + plane.w) / 
		   abs(sqrt(plane.x * plane.x + plane.y * plane.y + plane.z * plane.z));
}

void main()
{		
	float dist = length(eyePosition - inWorldPos);
	float height = inWorldPos.y;
	
	// normalmap/occlusionmap/splatmap coords
	vec2 mapCoords = (inWorldPos.xz + scaleXZ/2)/scaleXZ; 
	vec3 normal = texture(normalmap, mapCoords).rgb;
	normal = normalize(normal);
	
	vec4 v_splatmap = texture(splatmap, mapCoords).rgba;
	float[4] blendValues = float[](v_splatmap.r,v_splatmap.g,v_splatmap.b,v_splatmap.a);
	
	if (dist < largeDetailRange-50)
	{
		float attenuation = clamp(-dist/(largeDetailRange-50) + 1,0.0,1.0);
		
		vec3 bitangent = normalize(cross(inTangent, normal));
		mat3 TBN = mat3(inTangent,bitangent,normal);
		
		vec3 bumpNormal;
		
		for (int i=0; i<3; i++){
			bumpNormal += (2*(texture(materials[i].normalmap, inUV/materials[i].horizontalScaling).rgb) - 1) * blendValues[i];
		}
		
		bumpNormal = normalize(bumpNormal);
		bumpNormal.xy *= attenuation;
		normal = normalize(TBN * bumpNormal);
	}
	
	vec3 fragColor = vec3(0,0,0);
	
	for (int i=0; i<3; i++){
		fragColor +=  texture(materials[i].diffusemap, inUV/materials[i].horizontalScaling).rgb
					* blendValues[i];
	}
	
	// caustics
	if (isCameraUnderWater == 1 && isRefraction == 0){
		vec2 causticsUV = inWorldPos.xz / 100;
		vec2 causticDistortion = texture(dudvCaustics, causticsUV*0.2 + distortionCaustics*0.6).rb * 0.18;
		vec3 causticsColor = texture(caustics, causticsUV + causticDistortion).rbg;
		
		fragColor += (causticsColor/5);
	}
	
	// underwater distance blue blur
	if (isCameraUnderWater == 0 && isRefraction == 1){
		
		float distToWaterSurace = distancePointPlane(inWorldPos,clipplane);
		float refractionFactor = clamp(0.025 * distToWaterSurace,0,1);
		
		fragColor = mix(fragColor, waterRefractionColor, refractionFactor); 
	}
	
	outAlbedo = vec4(fragColor,1);
	outWorldPos = vec4(inWorldPos,1);
	outNormal = vec4(normal,1);
	outSpecularEmission = vec4(1,0,0,1);
	outLightScattering = vec4(0,0,0,1);
}