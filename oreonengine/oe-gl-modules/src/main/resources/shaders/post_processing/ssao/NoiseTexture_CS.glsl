#version 430

layout (local_size_x = 4, local_size_y = 4) in;

layout (binding = 0, rgba32f) uniform writeonly image2D outputNoise;

uniform int randomx[16];
uniform int randomy[16];

void main(void){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.xy);
	
	vec3 noise = vec3(
					randomx[computeCoord.x * 4 + computeCoord.y],
					randomy[computeCoord.x * 4 + computeCoord.y],
					0.0f);
					
	noise = normalize(noise);
	
	imageStore(outputNoise, computeCoord, vec4(noise,1.0));
}