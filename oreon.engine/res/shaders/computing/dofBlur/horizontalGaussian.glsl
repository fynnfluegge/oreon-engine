#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba8) uniform readonly image2D sceneSampler;

layout (binding = 1, rgba8) uniform writeonly image2D horizontalBlur;

uniform sampler2D depthmap;
uniform float windowWidth;
uniform float windowHeight;
uniform float gaussianKernel3[3];
uniform float gaussianKernel5[5];
uniform float gaussianKernel7[7];
uniform float gaussianKernel9[9];

float zfar = 10000.0f;
float znear = 0.1f;

float linearize(float depth)
{
	return (2 * znear) / (zfar + znear - depth * (zfar - znear));
}

void main(void){

	// window coords
	vec2 w = vec2(gl_GlobalInvocationID.x/windowWidth, gl_GlobalInvocationID.y/windowHeight);
	
	// Get the depth buffer value at this pixel.  
	float depth = texture(depthmap, w).r ; 
	
	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
	
	float linDepth = linearize(depth);
	
	vec3 color = imageLoad(sceneSampler, computeCoord).rgb;  
	
	if (linDepth > 0.002){
		color = vec3(0,0,0);
		color += imageLoad(sceneSampler, computeCoord + ivec2(-4,0)).rgb * gaussianKernel9[0];
		color += imageLoad(sceneSampler, computeCoord + ivec2(-3,0)).rgb * gaussianKernel9[1]; 
		color += imageLoad(sceneSampler, computeCoord + ivec2(-2,0)).rgb * gaussianKernel9[2]; 
		color += imageLoad(sceneSampler, computeCoord + ivec2(-1,0)).rgb * gaussianKernel9[3]; 
		color += imageLoad(sceneSampler, computeCoord).rgb * gaussianKernel9[4]; 
		color += imageLoad(sceneSampler, computeCoord + ivec2(1,0)).rgb * gaussianKernel9[5];
		color += imageLoad(sceneSampler, computeCoord + ivec2(2,0)).rgb * gaussianKernel9[6];
		color += imageLoad(sceneSampler, computeCoord + ivec2(3,0)).rgb * gaussianKernel9[7];
		color += imageLoad(sceneSampler, computeCoord + ivec2(4,0)).rgb * gaussianKernel9[8];
	}
	
	imageStore(horizontalBlur, computeCoord, vec4(color, 1.0));
}