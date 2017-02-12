#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba16f) uniform writeonly image2D AdditiveBlendedBloomSampler;

uniform sampler2D bloomBlurSampler_div2;
uniform sampler2D bloomBlurSampler_div4;
uniform sampler2D bloomBlurSampler_div8;
uniform sampler2D bloomBlurSampler_div16;
uniform float windowWidth;
uniform float windowHeight;

void main(void){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
	vec2 texCoord = vec2(gl_GlobalInvocationID.x/windowWidth,gl_GlobalInvocationID.y/windowHeight);

	vec3 color = vec3(0,0,0);
	color += texture(bloomBlurSampler_div2, texCoord).rgb;
	color += texture(bloomBlurSampler_div4, texCoord).rgb;
	color += texture(bloomBlurSampler_div8, texCoord).rgb;
	color += texture(bloomBlurSampler_div16, texCoord).rgb;
	
	color /= 4;

	imageStore(AdditiveBlendedBloomSampler, computeCoord, vec4(color, 1.0));

}