#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba16f) uniform writeonly image2D defferedSceneImage;

layout (binding = 1, rgba16f) uniform readonly image2D albedoSceneImage;

layout (binding = 2, rgba32f) uniform readonly image2D normalImage;

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

float diffuse(vec3 direction, vec3 normal, float intensity)
{
	return max(0.2, dot(normal, -direction) * intensity);
}

void main(void){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
	
	vec3 albedo = imageLoad(albedoSceneImage, computeCoord).rgb;
	vec3 normal = imageLoad(normalImage, computeCoord).rbg;
	vec3 finalColor = albedo;
	
	// prevent lighting sky
	if (normal != vec3(0,0,0)){
	
		float diff = diffuse(directional_light.direction, normal, directional_light.intensity);

		vec3 diffuseLight = directional_light.ambient + directional_light.color * diff;

		finalColor = albedo * diffuseLight;
	}
	
	imageStore(defferedSceneImage, computeCoord, vec4(finalColor,1.0));
}