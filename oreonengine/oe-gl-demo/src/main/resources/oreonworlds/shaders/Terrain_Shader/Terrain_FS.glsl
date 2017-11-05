#version 430

in vec2 texCoordF;
in vec4 viewSpacePos;
in vec3 position;
in vec3 tangent;

layout(location = 0) out vec4 outputColor;
layout(location = 4) out vec4 blackColor;

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

layout (std140) uniform DirectionalLight{
	vec3 direction;
	float intensity;
	vec3 ambient;
	vec3 color;
} directional_light;

layout (std140, row_major) uniform LightViewProjections{
	mat4 m_lightViewProjection[6];
	float splitRange[6];
};

uniform sampler2DArray shadowMaps;
uniform Fractal fractals1[7];
uniform sampler2D splatmap;
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

float diffuse(vec3 direction, vec3 normal, float intensity)
{
	return max(0.1, dot(normal, -direction) * intensity);
}

float specular(vec3 direction, vec3 normal, vec3 eyePosition, vec3 vertexPosition)
{
	vec3 reflectionVector = normalize(reflect(direction, normal));
	vec3 vertexToEye = normalize(eyePosition - vertexPosition);
	
	float specular = max(0, dot(vertexToEye, reflectionVector));
	
	return pow(specular, shininess) * emission;
}

float linearizeDepth(float depth)
{
	return (2 * znear) / (zfar + znear - depth * (zfar - znear));
}

float percentageCloserShadows(vec3 projCoords, int split, float shadowFactor){

	float currentDepth = projCoords.z;
	float shadowMapDepth = texture(shadowMaps, vec3(projCoords.xy,split)).r;
	
	float dist = linearizeDepth(shadowMapDepth) - linearizeDepth(currentDepth);
		
	if (dist < 0)
		return 0;
	else 
		return 1;
}

float varianceShadow(vec3 projCoords, int split, int kernels){
	
	float shadowFactor = 1.0;
	float texelSize = 1.0/ 4096.0;
	float currentDepth = projCoords.z;
	float reduceFactor = 1/ pow(kernels*2+1,2);
	
	for (int i=-kernels; i<=kernels; i++){
		for (int j=-kernels; j<=kernels; j++){
			float shadowMapDepth = texture(shadowMaps, vec3(projCoords.xy,split)
													   + vec3(i,j,0) * texelSize).r; 
			if (currentDepth > shadowMapDepth)
				shadowFactor -= reduceFactor;
		}
	}
	
	return max(0.0,shadowFactor);
}


float shadow(vec3 worldPos)
{
	float shadow = 1;
	float shadowFactor = 0;
	vec3 projCoords = vec3(0,0,0);
	float depth = viewSpacePos.z/zfar;
	if (depth < splitRange[0]){
		vec4 lightSpacePos = m_lightViewProjection[0] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor0 = varianceShadow(projCoords,0,4);
		
		lightSpacePos = m_lightViewProjection[1] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor1 = varianceShadow(projCoords,1,4);
		
		lightSpacePos = m_lightViewProjection[2] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor2 = varianceShadow(projCoords,2,2);
		
		shadowFactor = min(shadowFactor2, min(shadowFactor0,shadowFactor1));
	}
	else if (depth < splitRange[1]){
		vec4 lightSpacePos = m_lightViewProjection[1] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor0 = varianceShadow(projCoords,1,4);
		
		lightSpacePos = m_lightViewProjection[2] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor1 = varianceShadow(projCoords,2,2);
		
		shadowFactor = min(shadowFactor0,shadowFactor1);
	}
	else if (depth < splitRange[2]){
		vec4 lightSpacePos = m_lightViewProjection[2] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,2,2);
	}
	else if (depth < splitRange[3]){
		vec4 lightSpacePos = m_lightViewProjection[3] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,3,2);
	}
	else if (depth < splitRange[4]){
		vec4 lightSpacePos = m_lightViewProjection[4] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,4,1); 
	}
	else if (depth < splitRange[5]){
		vec4 lightSpacePos = m_lightViewProjection[5] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,5,1); 
	}
	else return 1;
	
	return shadowFactor;
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
	
	blendNormal += (2*(texture(fractals1[0].normalmap, mapCoords*fractals1[0].scaling).rbg)-1);
	blendNormal += (2*(texture(fractals1[1].normalmap, mapCoords*fractals1[1].scaling).rbg)-1);
	blendNormal += (2*(texture(fractals1[2].normalmap, mapCoords*fractals1[2].scaling).rbg)-1);
	blendNormal += (2*(texture(fractals1[3].normalmap, mapCoords*fractals1[3].scaling).rbg)-1);
	normal = blendNormal;
	normal += (2*(texture(fractals1[4].normalmap, mapCoords*fractals1[4].scaling).rbg)-1);
	normal += (2*(texture(fractals1[5].normalmap, mapCoords*fractals1[5].scaling).rbg)-1);
	normal += (2*(texture(fractals1[6].normalmap, mapCoords*fractals1[6].scaling).rbg)-1);
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
		float attenuation = -dist/(largeDetailedRange-20) + 1;
		
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
	
	float diffuse = diffuse(directional_light.direction, bumpNormal, directional_light.intensity);
	float specular = specular(directional_light.direction, bumpNormal, eyePosition, position);
	
	vec3 specularLight = directional_light.color * specular;
	vec3 diffuseLight = vec3(0,0,0);
	
	if (isReflection == 1){
		diffuseLight = directional_light.ambient + directional_light.color * diffuse;
	}
	else {
		diffuseLight = directional_light.ambient + directional_light.color * (diffuse * shadow(position));
	}
	
	
	vec3 fragColor = mix(texture(sand.diffusemap, texCoordF).rgb, texture(rock.diffusemap, texCoordF/20).rgb, rockBlend);
	
	fragColor = mix(fragColor, texture(cliff.diffusemap,texCoordF/20).rgb, cliffBlend);
	if (blendNormal.y > 0.95){
		fragColor = mix(fragColor,texture(grass.diffusemap,texCoordF).rgb,grassBlend);// * clamp(-(height-400)/200,0,1));
	}
	if (blendNormal.y < 0.99){
		fragColor = mix(fragColor,texture(cliff.diffusemap,texCoordF/20).rgb,cliffSlopeFactor);
	}
	
	fragColor *= diffuseLight;
	fragColor += specularLight;
	
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
	
	outputColor = vec4(rgb,1);
	blackColor = vec4(0,0,0,1);
}