#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba16f) uniform writeonly image2D motionBlur;

layout (binding = 1, rgba16f) uniform readonly image2D sceneSampler;

layout (binding = 2, rgba32f) uniform readonly image2D velocityMap;

uniform float windowWidth;
uniform float windowHeight;

float maxSamples = 12;

void main(void)
{
	vec2 texCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
	
	// Get the initial color and velocity at this pixel. 
	vec2 velocity = imageLoad(velocityMap, ivec2(gl_GlobalInvocationID.xy)).rg;
	vec4 blurredColor = imageLoad(sceneSampler, ivec2(gl_GlobalInvocationID.xy));  

	texCoord += velocity; 
		
	float samples = 1;
	vec4 currentColor;
	
		//Sample the color buffer along the velocity vector. 
		for(int i = 1; i < maxSamples; ++i)  
		{  
				if (texCoord.x >= 0 && texCoord.y >= 0 && texCoord.x < windowWidth && texCoord.y < windowHeight){
					currentColor = imageLoad(sceneSampler, ivec2(texCoord.x, texCoord.y));  
					blurredColor += currentColor;
					texCoord += velocity;
					samples++;
				}
				if (texCoord.x < 0 && texCoord.y >= 0 && texCoord.y < windowHeight){
					currentColor = imageLoad(sceneSampler, ivec2(0, texCoord.y));  
					blurredColor += currentColor;
					texCoord += vec2(0,velocity.y);
					samples++;
				}
				if (texCoord.x >= windowWidth && texCoord.y >= 0 && texCoord.y < windowHeight){
					currentColor = imageLoad(sceneSampler, ivec2(windowWidth-1, texCoord.y));  
					blurredColor += currentColor;
					texCoord += vec2(0,velocity.y);
					samples++;
				}
				if (texCoord.y < 0 && texCoord.x >= 0 && texCoord.x < windowWidth){
					currentColor = imageLoad(sceneSampler, ivec2(texCoord.x,0));  
					blurredColor += currentColor;
					texCoord += vec2(velocity.x,0);
					samples++;
				}
				if (texCoord.y >= windowHeight && texCoord.x >= 0 && texCoord.x < windowWidth){
					currentColor = imageLoad(sceneSampler, ivec2(texCoord.x, windowHeight-1));  
					blurredColor += currentColor;
					texCoord += vec2(velocity.x,0);
					samples ++;
				}
		}  
		
	// Average all of the samples to get the final blur color.  
   vec4 finalColor = blurredColor / samples;
   imageStore(motionBlur, ivec2(gl_GlobalInvocationID.xy), finalColor);
}   

