#version 430 core

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba16f) uniform writeonly image2D defferedSceneImage;

layout (binding = 1, rgba8) uniform readonly image2DMS albedoSceneImage;

layout (binding = 2, rgba32f) uniform readonly image2DMS worldPositionImage;

layout (binding = 3, rgba32f) uniform readonly image2DMS normalImage;

layout (binding = 4, rgba8) uniform readonly image2DMS specularEmissionImage;

layout (binding = 5, rgba8) uniform readonly image2DMS sampleCoverageMask;

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

const int multisamples = 8;

void main(void){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
	
	vec3 albedo = imageLoad(albedoSceneImage, computeCoord,0).rgb; 
	vec3 position = imageLoad(worldPositionImage, computeCoord,0).rgb;
	vec3 normal = imageLoad(normalImage, computeCoord,0).rbg;
	vec2 specular_emission = imageLoad(specularEmissionImage, computeCoord,0).rg;
	
	vec3 finalColor = albedo;
	
	// prevent lighting sky
	if (imageLoad(normalImage, computeCoord,0).a != 0.0){
	
		float diff = diffuse(directional_light.direction, normal, directional_light.intensity);
		float spec = specular(directional_light.direction, normal, eyePosition, position, specular_emission.r, specular_emission.g);

		vec3 diffuseLight = directional_light.ambient + directional_light.color * diff;
		vec3 specularLight = directional_light.color * spec;

		finalColor = albedo * diffuseLight + specularLight;
	}
	
	float abc = imageLoad(sampleCoverageMask, computeCoord,0).r; 
	abc += imageLoad(sampleCoverageMask, computeCoord,1).r; 
	abc += imageLoad(sampleCoverageMask, computeCoord,2).r; 
	abc += imageLoad(sampleCoverageMask, computeCoord,3).r; 
	abc += imageLoad(sampleCoverageMask, computeCoord,4).r; 
	abc += imageLoad(sampleCoverageMask, computeCoord,5).r; 
	abc += imageLoad(sampleCoverageMask, computeCoord,6).r; 
	abc += imageLoad(sampleCoverageMask, computeCoord,7).r; 	
	
	imageStore(defferedSceneImage, computeCoord, vec4(abc,0.0,0.0,1.0));
}