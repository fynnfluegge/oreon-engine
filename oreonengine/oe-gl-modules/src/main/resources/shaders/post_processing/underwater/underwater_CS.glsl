#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba16f) uniform readonly image2D sceneSampler;

layout (binding = 1, rgba16f) uniform writeonly image2D underwaterSceneSampler;

uniform sampler2D sceneDepthMap;
uniform float windowWidth;
uniform float windowHeight;

const float zfar = 10000.0f;
const float znear = 0.1f;

const vec3 waterRefractionColor = vec3(0.1,0.125,0.19);

float linearize(float depth)
{
	return (2 * znear) / (zfar + znear - depth * (zfar - znear));
}

void main(){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
	
	// window coords
	vec2 w = vec2(gl_GlobalInvocationID.x/windowWidth, gl_GlobalInvocationID.y/windowHeight);
	
	// Get the depth buffer value at this pixel.  
	float depth = linearize(texture(sceneDepthMap, w).r); 
	
	vec3 sceneColor = imageLoad(sceneSampler, computeCoord).rgb;
	
	float refractionFactor = clamp(2 * pow(depth,0.2) + 0.1,0.0,0.96);
	
	vec3 rgb = mix(sceneColor, waterRefractionColor, refractionFactor); 
	
	
	imageStore(underwaterSceneSampler, computeCoord, vec4(rgb,1));
}