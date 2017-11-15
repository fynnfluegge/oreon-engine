#version 430

in vec2 texCoordF;
in vec4 viewSpacePos;
in vec3 position;
in vec3 tangent;

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 worldPosition_out;
layout(location = 2) out vec4 normal_out;
layout(location = 3) out vec4 specularEmission_out;
layout(location = 4) out vec4 lightScattering_out;

struct Material
{
	sampler2D diffusemap;
	sampler2D normalmap;
	sampler2D heightmap;
	sampler2D splatmap;
	float displaceScale;
};

struct Fractal
{
	sampler2D normalmap;
	int scaling;
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

uniform sampler2D normalmap;
uniform Fractal fractals1[1];
uniform float scaleY;
uniform float scaleXZ;
uniform Material sand;
uniform Material grass;
uniform Material rock;
uniform Material cliff;
uniform float sightRangeFactor;
uniform int largeDetailedRange;
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
	float dist = length(eyePosition - position);
	float height = position.y;
	
	// normalmap/occlusionmap/splatmap coords
	vec2 mapCoords = (position.xz + scaleXZ/2)/scaleXZ; 

	vec3 bumpNormal = vec3(0,0,0);
	
	vec3 normal = texture(normalmap, mapCoords).rgb;
	// normal.xy += (texture(fractals1[0].normalmap, mapCoords*fractals1[0].scaling).rg);
	normal = normalize(normal);
	
	float grassBlend = texture(grass.splatmap, mapCoords).r;
	float sandBlend = texture(sand.splatmap, mapCoords).r;
	float rockBlend = texture(rock.splatmap, mapCoords).r;
	float cliffBlend = texture(cliff.splatmap, mapCoords).r;
	
	vec3 grassColor = texture(grass.diffusemap, texCoordF).rgb;
	vec3 sandColor = texture(sand.diffusemap, texCoordF/2).rgb;
	vec3 rockColor = texture(rock.diffusemap, texCoordF/20).rgb;
	vec3 cliffColor = texture(cliff.diffusemap, texCoordF/20).rgb;
	
	if (dist < largeDetailedRange-50 && isReflection == 0)
	{
		float attenuation = clamp(-dist/(largeDetailedRange-50) + 1,0.0,1.0);
		
		vec3 bitangent = normalize(cross(tangent, normal));
		mat3 TBN = mat3(tangent,bitangent,normal);
		
		vec3 bumpnormal = normalize((2*(texture(grass.normalmap, texCoordF).rgb) - 1) * grassBlend
								 +  (2*(texture(sand.normalmap, texCoordF/2).rgb) - 1) * sandBlend
								 +  (2*(texture(rock.normalmap, texCoordF/10).rgb) - 1) * rockBlend
								 +  (2*(texture(cliff.normalmap, texCoordF/10).rgb) - 1) * cliffBlend);
		
		bumpnormal.xy *= attenuation;
		
		normal = normalize(TBN * bumpnormal);
	}
	
	vec3 fragColor = grassColor * grassBlend + 
				     sandColor * sandBlend + 
					 rockColor * rockBlend + 
					 cliffColor * cliffBlend;
	
	// caustics
	if (isCameraUnderWater == 1 && isRefraction == 0){
		vec2 causticsTexCoord = position.xz / 100;
		vec2 causticDistortion = texture(dudvCaustics, causticsTexCoord*0.2 + distortionCaustics*0.6).rb * 0.18;
		vec3 causticsColor = texture(caustics, causticsTexCoord + causticDistortion).rbg;
		
		fragColor += (causticsColor/5);
	}
	float fogFactor = -0.0002/sightRangeFactor*(dist-(zfar)/10*sightRangeFactor) + 1;

    vec3 rgb = mix(fogColor, fragColor, clamp(fogFactor,0,1));
	
	albedo_out = vec4(rgb,1);
	worldPosition_out = vec4(position,1);
	normal_out = vec4(normal,1);
	specularEmission_out = vec4(1,0,0,1);
	lightScattering_out = vec4(0,0,0,1);
}