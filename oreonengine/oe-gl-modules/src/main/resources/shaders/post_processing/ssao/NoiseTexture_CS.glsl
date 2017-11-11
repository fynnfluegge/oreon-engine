#version 430

layout (local_size_x = 4, local_size_y = 4) in;

layout (binding = 0, rgba32f) uniform writeonly image2D outputNoise;

uniform float randomx[16];
uniform float randomy[16];

void main(void){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.xy);
	
	vec3 noise = vec3(randomx[computeCoord.x + computeCoord.y * 4],
					  randomy[computeCoord.x + computeCoord.y * 4],
					  0.0f);
					
	noise = normalize(noise);
	
	imageStore(outputNoise, computeCoord, vec4(noise,1.0));
}