#version 430 core

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba16f) uniform writeonly image2D defferedSceneImage;

layout (binding = 1, rgba32f) uniform writeonly image2D depthImage;

layout (binding = 2, rgba16f) uniform readonly image2DMS albedoSceneImage;

layout (binding = 3, rgba32f) uniform readonly image2DMS worldPositionImage;

layout (binding = 4, rgba32f) uniform readonly image2DMS normalImage;

layout (binding = 5, rgba8)   uniform readonly image2DMS specularEmissionImage;

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

uniform sampler2DMS depthmap;
uniform int multisamples;

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

void main(void){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
	
	vec3 albedo = vec3(0,0,0);
	vec3 position = vec3(0,0,0);
	vec3 normal = vec3(0,0,0);
	vec2 specular_emission = vec2(0,0);
	vec3 depth = vec3(0,0,0);
	
	if(imageLoad(sampleCoverageMask, computeCoord).r == 1.0){
	
		// perform supersampling
		
		for (int i=0; i<multisamples; i++){
			albedo += imageLoad(albedoSceneImage, computeCoord,i).rgb; 
		}
		albedo /= multisamples;
		
		for (int i=0; i<multisamples; i++){
			position += imageLoad(worldPositionImage, computeCoord,i).rgb; 
		}
		position /= multisamples;
		
		for (int i=0; i<multisamples; i++){
			normal += imageLoad(normalImage, computeCoord,i).rbg; 
		}
		normal /= multisamples;
		normal = normalize(normal);
		
		for (int i=0; i<multisamples; i++){
			specular_emission += imageLoad(specularEmissionImage, computeCoord,i).rg; 
		}
		specular_emission /= multisamples;
		
		for (int i=0; i<multisamples; i++){
			depth += texelFetch(depthmap, computeCoord, i).rgb;
		}
		depth /= multisamples;
	}
	else {
		albedo = imageLoad(albedoSceneImage, computeCoord,0).rgb;
		position = imageLoad(worldPositionImage, computeCoord,0).rgb;
		normal = imageLoad(normalImage, computeCoord,0).rbg;
		specular_emission = imageLoad(specularEmissionImage, computeCoord,0).rg;
		depth = texelFetch(depthmap, computeCoord, 0).rgb;
	}
		
	vec3 finalColor = albedo;
	
	// prevent lighting sky
	if (imageLoad(normalImage, computeCoord,0).rgb != vec3(0,0,0)){
	
		float diff = diffuse(directional_light.direction, normal, directional_light.intensity);
		float spec = specular(directional_light.direction, normal, eyePosition, position, specular_emission.r, specular_emission.g);

		vec3 diffuseLight = directional_light.ambient + directional_light.color * diff;
		vec3 specularLight = directional_light.color * spec;

		vec3 ssao = imageLoad(ssaoBlurImage, computeCoord).rgb;
		finalColor = albedo * diffuseLight * ssao + specularLight;
	}
	
	imageStore(defferedSceneImage, computeCoord, vec4(finalColor,1.0));
	imageStore(depthImage, computeCoord, vec4(depth,1.0));
}