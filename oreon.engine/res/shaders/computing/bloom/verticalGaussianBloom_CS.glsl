#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba16f) uniform readonly image2D horizontalBloomBlurSceneSampler;

layout (binding = 1, rgba16f) uniform writeonly image2D verticalBloomBlurSceneSampler;


const float gaussianKernel21_sigma4[21] = float[21](0.004481,0.008089,0.013722,0.021874,0.032768,0.046128,0.061021,0.075856,0.088613,0.097274,0.100346,0.097274,0.088613,0.075856,0.061021,0.046128,0.032768,0.021874,0.013722,0.008089,0.004481);

void main(void){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
	
	vec3 color = vec3(0,0,0);
	for (int i=0; i<21; i++){
		color += imageLoad(horizontalBloomBlurSceneSampler, computeCoord + ivec2(0,i-10)).rgb * gaussianKernel21_sigma4[i];
	}

	imageStore(verticalBloomBlurSceneSampler, computeCoord, vec4(color, 1.0));
}