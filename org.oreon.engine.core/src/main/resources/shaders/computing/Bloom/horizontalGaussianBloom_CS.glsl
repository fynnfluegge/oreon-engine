#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba16f) uniform writeonly image2D horizontalBloomBlurSampler;

uniform sampler2D bloomSampler;
uniform float windowWidth;
uniform float windowHeight;
uniform int div;

const float gaussianKernel7_sigma2[7] = float[7](0.071303,0.131514,0.189879,0.214607,0.189879,0.131514,0.071303);
const float gaussianKernel9_sigma3[9] = float[9](0.063327,0.093095,0.122589,0.144599,0.152781,0.144599,0.122589,0.093095,0.063327);
const float gaussianKernel9_sigma4[9] = float[9](0.081812,0.101701,0.118804,0.130417,0.134535,0.130417,0.118804,0.101701,0.081812);

void main(void){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
	vec2 texCoord = vec2(gl_GlobalInvocationID.x/windowWidth,gl_GlobalInvocationID.y/windowHeight);
	
	vec3 color = vec3(0,0,0);
	for (int i=0; i<9; i++){
		color += texture(bloomSampler, texCoord + vec2((i-4)/windowWidth,0)).rgb * gaussianKernel9_sigma4[i];
	}

	imageStore(horizontalBloomBlurSampler, computeCoord, vec4(color, 1.0));
}