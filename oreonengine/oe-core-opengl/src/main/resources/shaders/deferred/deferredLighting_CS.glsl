#version 430 core

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba16f) uniform writeonly image2D defferedSceneImage;

layout (binding = 1, rgba32f) uniform writeonly image2D depthImage;

layout (binding = 2, rgba16f) uniform readonly image2DMS albedoSceneImage;

layout (binding = 3, rgba32f) uniform readonly image2DMS worldPositionImage;

layout (binding = 4, rgba32f) uniform readonly image2DMS normalImage;

layout (binding = 5, rgba32f)   uniform readonly image2DMS specularEmissionImage;

layout (binding = 6, r32f) uniform readonly image2D sampleCoverageMask;

layout (binding = 7, rgba32f) uniform readonly image2D ssaoBlurImage;

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

uniform sampler2DArray pssm;
uniform sampler2DMS depthmap;
uniform int numSamples;

const float zfar = 10000;
const float znear = 0.1;
const vec3 fogColor = vec3(0.65,0.85,0.9);

float diffuse(vec3 direction, vec3 normal, float intensity)
{
	return max(0.0, dot(normal, -direction) * intensity);
}

float specular(vec3 direction, vec3 normal, vec3 eyePosition, vec3 vertexPosition, float specularFactor, float emissionFactor)
{
	vec3 reflectionVector = normalize(reflect(direction, normal));
	vec3 vertexToEye = normalize(eyePosition - vertexPosition);
	
	float specular = max(0.0, dot(vertexToEye, reflectionVector));
	
	return pow(specular, specularFactor) * emissionFactor;
}

float linearizeDepth(float depth)
{
	return (2 * znear) / (zfar + znear - depth * (zfar - znear));
}

float percentageCloserShadows(vec3 projCoords, int split, float shadowFactor){

	float currentDepth = projCoords.z;
	float shadowMapDepth = texture(pssm, vec3(projCoords.xy,split)).r;
	
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
			float shadowMapDepth = texture(pssm, vec3(projCoords.xy,split)
													   + vec3(i,j,0) * texelSize).r; 
			if (currentDepth > shadowMapDepth)
				shadowFactor -= reduceFactor;
		}
	}
	
	return max(0.1,shadowFactor);
}


float applyShadowMapping(vec3 worldPos, float depth)
{
	float shadowFactor = 0;
	vec3 projCoords = vec3(0,0,0);
	float linDepth = (m_View * vec4(worldPos,1)).z/zfar;
	if (linDepth < splitRange[0]){
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
	else if (linDepth < splitRange[1]){
		vec4 lightSpacePos = m_lightViewProjection[1] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor0 = varianceShadow(projCoords,1,4);
		
		lightSpacePos = m_lightViewProjection[2] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor1 = varianceShadow(projCoords,2,2);
		
		shadowFactor = min(shadowFactor0,shadowFactor1);
	}
	else if (linDepth < splitRange[2]){
		vec4 lightSpacePos = m_lightViewProjection[2] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,2,2);
	}
	else if (linDepth < splitRange[3]){
		vec4 lightSpacePos = m_lightViewProjection[3] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,3,2);
	}
	else if (linDepth < splitRange[4]){
		vec4 lightSpacePos = m_lightViewProjection[4] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,4,1); 
	}
	else if (linDepth < splitRange[5]){
		vec4 lightSpacePos = m_lightViewProjection[5] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,5,1); 
	}
	else return 1;
	
	return shadowFactor;
}

void main(void){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
	
	vec3 finalColor = vec3(0,0,0);
	vec3 albedo = vec3(0,0,0);
	vec3 position = vec3(0,0,0);
	vec3 normal = vec3(0,0,0);
	vec2 specular_emission = vec2(0,0);
	vec3 depth = vec3(0,0,0);
	
	float diff = 0;
	float shadow = 0;
	float spec = 0;
	
	if(imageLoad(sampleCoverageMask, computeCoord).r == 1.0){
		
		for (int i=0; i<numSamples; i++){
			
			albedo = imageLoad(albedoSceneImage, computeCoord,i).rgb; 
			normal = imageLoad(normalImage, computeCoord,i).rbg; 
			
			// prevent lighting atmosphere
			if (normal != vec3(0,0,0)){
				position = imageLoad(worldPositionImage, computeCoord,i).rgb; 
				specular_emission = imageLoad(specularEmissionImage, computeCoord,i).rg; 
			
				diff = diffuse(directional_light.direction, normal, directional_light.intensity);
				spec = specular(directional_light.direction, normal, eyePosition, position, specular_emission.r, specular_emission.g);
				shadow = applyShadowMapping(position, depth.r);
				
				vec3 diffuseLight = directional_light.ambient + directional_light.color * diff;
				vec3 specularLight = directional_light.color * spec;
				vec3 ssao = imageLoad(ssaoBlurImage, computeCoord).rgb;
			
				finalColor += (albedo * diffuseLight * shadow * ssao + specularLight);
			}
			else{
				finalColor += albedo;
			}
		}
		
		finalColor /= numSamples;
		
		for (int i=0; i<numSamples; i++){
			depth += texelFetch(depthmap, computeCoord, i).rgb;
		}
		depth /= numSamples;
	}
	else {
		albedo = imageLoad(albedoSceneImage, computeCoord,0).rgb;
		position = imageLoad(worldPositionImage, computeCoord,0).rgb;
		normal = imageLoad(normalImage, computeCoord,0).rbg;
		depth = texelFetch(depthmap, computeCoord, 0).rgb;
		
		if (normal != vec3(0,0,0)){
			specular_emission = imageLoad(specularEmissionImage, computeCoord,0).rg;
			depth = texelFetch(depthmap, computeCoord,0).rgb;
		
			diff = diffuse(directional_light.direction, normal, directional_light.intensity);
			spec = specular(directional_light.direction, normal, eyePosition, position, specular_emission.r, specular_emission.g);
			shadow = applyShadowMapping(position, depth.r);
			
			vec3 diffuseLight = directional_light.ambient + directional_light.color * diff;
			vec3 specularLight = directional_light.color * spec;
			vec3 ssao = imageLoad(ssaoBlurImage, computeCoord).rgb;
			
			finalColor = albedo * diffuseLight * shadow * ssao + specularLight;
		}
		else{
			finalColor = albedo;
		}
	}
	
	imageStore(defferedSceneImage, computeCoord, vec4(finalColor,1.0));
	imageStore(depthImage, computeCoord, vec4(depth,1.0));
}