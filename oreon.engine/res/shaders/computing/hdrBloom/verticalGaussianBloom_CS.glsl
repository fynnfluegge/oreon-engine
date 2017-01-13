#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba16f) uniform readonly image2D horizontalBloomBlurSceneSampler;

layout (binding = 1, rgba16f) uniform writeonly image2D verticalBloomBlurSceneSampler;


const float gaussianKernel7_sigma2[7] = float[7](0.071303,0.131514,0.189879,0.214607,0.189879,0.131514,0.071303);

void main(void){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
	
	vec3 color = vec3(0,0,0);
	for (int i=0; i<7; i++){
		color += imageLoad(horizontalBloomBlurSceneSampler, computeCoord + ivec2(0,i-3)).rgb * gaussianKernel7_sigma2[i];
	}

	imageStore(verticalBloomBlurSceneSampler, computeCoord, vec4(color, 1.0));
}