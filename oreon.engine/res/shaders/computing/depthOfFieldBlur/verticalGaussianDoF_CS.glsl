#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba8) uniform readonly image2D horizontalBlurSceneSampler;

layout (binding = 1, rgba8) uniform writeonly image2D verticalBlurSceneSampler;

uniform sampler2D depthmap;
uniform float windowWidth;
uniform float windowHeight;
uniform float gaussianKernel7[7];

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
	
	vec3 color = imageLoad(horizontalBlurSceneSampler, computeCoord).rgb;  
	
	if (gl_GlobalInvocationID.x > 3 && gl_GlobalInvocationID.x > 3
		&& gl_GlobalInvocationID.x < windowWidth-4
		&& gl_GlobalInvocationID.y < windowHeight-4)
	{
	
		if (linDepth > 0.05) {
			color = vec3(0,0,0);
			
			for (int i=0; i<7; i++){
				if (linearize(texture(depthmap, w + vec2(0,((i-3)*2)/windowWidth)).r) > 0.049)
					color += imageLoad(horizontalBlurSceneSampler, computeCoord + ivec2(0,(i-3)*2)).rgb * gaussianKernel7[i];
				else
					color += imageLoad(horizontalBlurSceneSampler, computeCoord).rgb * gaussianKernel7[i];
			}
		}
		else if (linDepth > 0.025) {
			color = vec3(0,0,0);
			
			for (int i=0; i<7; i++){
				if (linearize(texture(depthmap, w + vec2(0,((i-3)*1.2)/windowWidth)).r) > 0.024)
					color += imageLoad(horizontalBlurSceneSampler, computeCoord + ivec2(0,(i-3)*1.2)).rgb * gaussianKernel7[i];
				else
					color += imageLoad(horizontalBlurSceneSampler, computeCoord).rgb * gaussianKernel7[i];
			}
		}
		else if (linDepth > 0.004){
			color = vec3(0,0,0);
			
			for (int i=0; i<7; i++){
				color += imageLoad(horizontalBlurSceneSampler, computeCoord + ivec2(0,i-3)).rgb * gaussianKernel7[i];
			}
		}
	}
	
	imageStore(verticalBlurSceneSampler, computeCoord, vec4(color, 1.0));
}