#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba16f) uniform readonly image2D horizontalBlurSceneSampler;

layout (binding = 1, rgba16f) uniform writeonly image2D verticalBlurSceneSampler;

uniform sampler2D depthmap;
uniform float windowWidth;
uniform float windowHeight;

const float gaussianKernel9_Sigma2[9]   = float[9](0.028532,0.067234,0.124009,0.179044,0.20236,0.179044,0.124009,0.067234,0.028532);
const float gaussianKernel7_Sigma1_5[7] = float[7](0.038735,0.113085,0.215007,0.266346,0.215007,0.113085,0.038735);
const float gaussianKernel7_Sigma1[7]   = float[7](0.00598,0.060626,0.241843,0.383103,0.241843,0.060626,0.00598);
const float zfar = 10000.0f;
const float znear = 0.1f;

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
	
	vec3 color = imageLoad(horizontalBlurSceneSampler, computeCoord).rgb;  
	
	if (gl_GlobalInvocationID.x > 3 && gl_GlobalInvocationID.y > 3
		&& gl_GlobalInvocationID.x < windowWidth-4
		&& gl_GlobalInvocationID.y < windowHeight-4)
	{
		if (linDepth > 0.06) {
			color = vec3(0,0,0);
			
			for (int i=0; i<9; i++){
				if (linearize(texture(depthmap, w + vec2(0,((i-4)*1.4)/windowWidth)).r) > 0.059)
					color += imageLoad(horizontalBlurSceneSampler, computeCoord + ivec2(0,(i-4)*1.4)).rgb * gaussianKernel9_Sigma2[i];
				else
					color += imageLoad(horizontalBlurSceneSampler, computeCoord).rgb * gaussianKernel9_Sigma2[i];
			}
		}
		else if (linDepth > 0.04) {
			color = vec3(0,0,0);
			
			for (int i=0; i<9; i++){
				if (linearize(texture(depthmap, w + vec2(0,(i-4)/windowWidth)).r) > 0.039)
					color += imageLoad(horizontalBlurSceneSampler, computeCoord + ivec2(0,i-4)).rgb * gaussianKernel9_Sigma2[i];
				else
					color += imageLoad(horizontalBlurSceneSampler, computeCoord).rgb * gaussianKernel9_Sigma2[i];
			}
		}
		else if (linDepth > 0.02) {
			color = vec3(0,0,0);
			
			for (int i=0; i<7; i++){
				if (linearize(texture(depthmap, w + vec2(0,(i-3)/windowWidth)).r) > 0.019)
					color += imageLoad(horizontalBlurSceneSampler, computeCoord + ivec2(0,i-3)).rgb * gaussianKernel7_Sigma1_5[i];
				else
					color += imageLoad(horizontalBlurSceneSampler, computeCoord).rgb * gaussianKernel7_Sigma1_5[i];
			}
		}
		else if (linDepth > 0.005){
			color = vec3(0,0,0);
			
			for (int i=0; i<7; i++){
				color += imageLoad(horizontalBlurSceneSampler, computeCoord + ivec2(0,i-3)).rgb * gaussianKernel7_Sigma1[i];
			}
		}
	}
	
	imageStore(verticalBlurSceneSampler, computeCoord, vec4(color, 1.0));
}