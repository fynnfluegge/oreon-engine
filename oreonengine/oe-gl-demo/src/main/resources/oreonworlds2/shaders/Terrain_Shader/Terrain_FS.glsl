#version 430

in vec2 texCoordF;
in vec4 viewSpacePos;
in vec3 position;
in vec3 tangent;

layout(location = 0) out vec4 albedoSampler;
layout(location = 1) out vec4 worldPositionSampler;
layout(location = 2) out vec4 normalSampler;
layout(location = 3) out vec4 specularEmissionSampler;

struct Material
{
	sampler2D diffusemap;
	sampler2D normalmap;
	sampler2D heightmap;
	float displaceScale;
	float shininess;
	float emission;
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
uniform sampler2D splatmap;
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

float emission;
float shininess;

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

	vec3 normal = vec3(0,0,0);
	vec3 bumpNormal = vec3(0,0,0);
	vec3 blendNormal = vec3(0,0,0);
	
	blendNormal += (2*(texture(normalmap, mapCoords).rbg) - 1);
	normal = blendNormal;
	normal += (texture(fractals1[0].normalmap, mapCoords*fractals1[0].scaling).rbg);
	normal = normalize(normal);
	blendNormal = normalize(blendNormal);
	bumpNormal = normal;
	
	float grassBlend = 0;
	float cliffBlend = 0;
	float rockBlend  = clamp((height+200)/200,0,1);
	float sandBlend   = clamp(-height/200,0,1);
	float cliffSlopeFactor = 0;
	
	// cliff Blending
	cliffSlopeFactor = 1-pow(blendNormal.y+0.01,12);
	cliffBlend += cliffSlopeFactor;
	cliffBlend = clamp(cliffBlend,0,1);
	rockBlend -= cliffSlopeFactor;
	rockBlend = clamp(rockBlend,0,1);
	sandBlend -= cliffSlopeFactor;
	sandBlend = clamp(sandBlend,0,1);
	
	// grass Blending
	if (blendNormal.y > 0.95){
		grassBlend = clamp(100*(blendNormal.y-0.95),0,1);
		
		rockBlend -= grassBlend;
		rockBlend = clamp(rockBlend,0.1,1.0);
		sandBlend -= grassBlend;
		sandBlend = clamp(sandBlend,0.1,1.0);
	}
	
	if (dist < largeDetailedRange-20 && isReflection == 0)
	{
		float attenuation = clamp(-dist/(largeDetailedRange-50) + 1,0.0,1.0);
		
		vec3 bitangent = normalize(cross(tangent, normal));
		mat3 TBN = mat3(tangent,normal,bitangent);
		
		bumpNormal = normalize((2*(texture(sand.normalmap, texCoordF).rbg) - 1) * sandBlend
								 +  (2*(texture(rock.normalmap, texCoordF/20).rbg) - 1) * rockBlend
								 +  (2*(texture(cliff.normalmap, texCoordF/20).rbg) - 1) * cliffBlend
								 +  ((2*(texture(grass.normalmap, texCoordF).rbg) - 1) * vec3(1,10,1)) * grassBlend);
		
		bumpNormal.xz *= attenuation;
		
		bumpNormal = normalize(TBN * bumpNormal);
	}
	
	emission  = sandBlend * sand.emission + rockBlend * rock.emission + cliffBlend * cliff.emission;
	shininess = sandBlend * sand.shininess + rockBlend * rock.shininess + cliffBlend * cliff.shininess;
	
	vec3 fragColor = mix(texture(sand.diffusemap, texCoordF).rgb, texture(rock.diffusemap, texCoordF/20).rgb, rockBlend);
	
	fragColor = mix(fragColor, texture(cliff.diffusemap,texCoordF/20).rgb, cliffBlend);
	if (blendNormal.y > 0.95){
		fragColor = mix(fragColor,texture(grass.diffusemap,texCoordF).rgb,grassBlend);// * clamp(-(height-400)/200,0,1));
	}
	if (blendNormal.y < 0.99){
		fragColor = mix(fragColor,texture(cliff.diffusemap,texCoordF/20).rgb,cliffSlopeFactor);
	}
	
	// caustics
	if (isCameraUnderWater == 1 && isRefraction == 0){
		vec2 causticsTexCoord = position.xz / 100;
		vec2 causticDistortion = texture(dudvCaustics, causticsTexCoord*0.2 + distortionCaustics*0.6).rb * 0.18;
		vec3 causticsColor = texture(caustics, causticsTexCoord + causticDistortion).rbg;
		
		fragColor += (causticsColor/5);
	}
	float fogFactor = -0.0002/sightRangeFactor*(dist-(zfar)/10*sightRangeFactor) + 1;

    vec3 rgb = mix(fogColor, fragColor, clamp(fogFactor,0,1));
	
	// underwater distance blue blur
	if (isRefraction == 1 && isCameraUnderWater == 0){
		
		float distToWaterSurace = distancePointPlane(position,clipplane);
		float refractionFactor = clamp(0.02 * distToWaterSurace,0,1);
		
		rgb = mix(rgb, waterRefractionColor, refractionFactor); 
	}
	
	albedoSampler = vec4(rgb,1);
	worldPositionSampler = vec4(position,1);
	normalSampler = vec4(bumpNormal.xzy,1);
	specularEmissionSampler = vec4(emission,shininess,0,1);
}