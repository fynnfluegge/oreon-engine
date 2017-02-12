#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba16f) uniform readonly image2D sceneSampler;

layout (binding = 1, rgba16f) uniform writeonly image2D bloomSampler;

void main(void){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);

	vec3 color = imageLoad(sceneSampler, computeCoord).rgb;  
	
	float brightness = dot(color, vec3(0.2126, 0.7152, 0.0722));

	vec3 brightColor = color * pow(brightness,2);
		
	imageStore(bloomSampler, computeCoord, vec4(brightColor, 1.0));
}