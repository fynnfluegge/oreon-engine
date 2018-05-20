#version 430 core

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba16f) uniform readonly image2D sceneSampler;

layout (binding = 1, rgba16f) uniform writeonly image2D contrastTexture;

uniform float contrastFactor;
uniform float brightnessFactor;

void main(void){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
	
	vec3 color = imageLoad(sceneSampler, computeCoord).rgb;  

	float newRed   = clamp((contrastFactor * ((color.r * 255) - 128) + 128) + brightnessFactor,0,255);
	float newGreen = clamp((contrastFactor * ((color.g * 255) - 128) + 128) + brightnessFactor,0,255);
	float newBlue  = clamp((contrastFactor * ((color.b * 255) - 128) + 128) + brightnessFactor,0,255);
	
	vec3 newColor = vec3(newRed,newGreen,newBlue) / 255;
		
	imageStore(contrastTexture, computeCoord, vec4(newColor, 1.0));
}