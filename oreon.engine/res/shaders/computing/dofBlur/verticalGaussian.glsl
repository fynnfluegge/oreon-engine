#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba8) uniform readonly image2D horizontalBlur;

layout (binding = 1, rgba8) uniform writeonly image2D depthOfFieldBlur;

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
	
	vec3 color = imageLoad(horizontalBlur, computeCoord).rgb;  
	
	if (gl_GlobalInvocationID.x > 3 && gl_GlobalInvocationID.x > 3
		&& gl_GlobalInvocationID.x < windowWidth-4
		&& gl_GlobalInvocationID.y < windowHeight-4)
	{
	
		if (linDepth > 0.08) {
			color = vec3(0,0,0);
			
			if (linearize(texture(depthmap, w + vec2(0,(-3*2)/windowWidth)).r) > 0.079)
				color += imageLoad(horizontalBlur, computeCoord + ivec2(0,-3*2)).rgb * gaussianKernel7[0];
			else
				color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[0];
			
			if (linearize(texture(depthmap, w + vec2(0,(-2*2)/windowWidth)).r) > 0.079)
				color += imageLoad(horizontalBlur, computeCoord + ivec2(0,-2*2)).rgb * gaussianKernel7[1];
			else
				color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[1];
				
			if (linearize(texture(depthmap, w + vec2(0,(-1*2)/windowWidth)).r) > 0.079)
				color += imageLoad(horizontalBlur, computeCoord + ivec2(0,-1*2)).rgb * gaussianKernel7[2]; 
			else
				color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[2];
				
			if (linearize(texture(depthmap, w).r) > 0.079)
				color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[3];
			else
				color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[3];
				
			if (linearize(texture(depthmap, w + vec2(0,(1*2)/windowWidth)).r) > 0.079)
				color += imageLoad(horizontalBlur, computeCoord + ivec2(0,1*2)).rgb * gaussianKernel7[4]; 
			else
				color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[4];
				
			if (linearize(texture(depthmap, w + vec2(0,(2*2)/windowWidth)).r) > 0.079)
				color += imageLoad(horizontalBlur, computeCoord + ivec2(0,2*2)).rgb * gaussianKernel7[5];
			else
				color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[5];
			
			if (linearize(texture(depthmap, w + vec2(0,(3*2)/windowWidth)).r) > 0.079)
				color += imageLoad(horizontalBlur, computeCoord + ivec2(0,3*2)).rgb * gaussianKernel7[6];
			else
				color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[6];	
		}
		if (linDepth > 0.04) {
			color = vec3(0,0,0);
			
			if (linearize(texture(depthmap, w + vec2(0,(-3*2)/windowWidth)).r) > 0.039)
				color += imageLoad(horizontalBlur, computeCoord + ivec2(0,-3*2)).rgb * gaussianKernel7[0];
			else
				color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[0];
			
			if (linearize(texture(depthmap, w + vec2(0,(-2*2)/windowWidth)).r) > 0.039)
				color += imageLoad(horizontalBlur, computeCoord + ivec2(0,-2*2)).rgb * gaussianKernel7[1];
			else
				color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[1];
				
			if (linearize(texture(depthmap, w + vec2(0,(-1*2)/windowWidth)).r) > 0.039)
				color += imageLoad(horizontalBlur, computeCoord + ivec2(0,-1*2)).rgb * gaussianKernel7[2]; 
			else
				color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[2];
				
			if (linearize(texture(depthmap, w).r) > 0.039)
				color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[3];
			else
				color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[3];
				
			if (linearize(texture(depthmap, w + vec2(0,(1*2)/windowWidth)).r) > 0.039)
				color += imageLoad(horizontalBlur, computeCoord + ivec2(0,1*2)).rgb * gaussianKernel7[4]; 
			else
				color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[4];
				
			if (linearize(texture(depthmap, w + vec2(0,(2*2)/windowWidth)).r) > 0.039)
				color += imageLoad(horizontalBlur, computeCoord + ivec2(0,2*2)).rgb * gaussianKernel7[5];
			else
				color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[5];
				
			if (linearize(texture(depthmap, w + vec2(0,(3*2)/windowWidth)).r) > 0.039)
				color += imageLoad(horizontalBlur, computeCoord + ivec2(0,3*2)).rgb * gaussianKernel7[6];
			else
				color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[6];
		}
		else if (linDepth > 0.004){
			color = vec3(0,0,0);
			color += imageLoad(horizontalBlur, computeCoord + ivec2(0,-3)).rgb * gaussianKernel7[0];
			color += imageLoad(horizontalBlur, computeCoord + ivec2(0,-2)).rgb * gaussianKernel7[1]; 
			color += imageLoad(horizontalBlur, computeCoord + ivec2(0,-1)).rgb * gaussianKernel7[2]; 
			color += imageLoad(horizontalBlur, computeCoord).rgb * gaussianKernel7[3]; 
			color += imageLoad(horizontalBlur, computeCoord + ivec2(0,1)).rgb * gaussianKernel7[4]; 
			color += imageLoad(horizontalBlur, computeCoord + ivec2(0,2)).rgb * gaussianKernel7[5];
			color += imageLoad(horizontalBlur, computeCoord + ivec2(0,3)).rgb * gaussianKernel7[6];
		}
	}
	
	imageStore(depthOfFieldBlur, computeCoord, vec4(color, 1.0));
}