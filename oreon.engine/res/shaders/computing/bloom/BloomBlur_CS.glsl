#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba16f) uniform readonly image2D sceneSampler;

layout (binding = 1, rgba16f) uniform readonly image2D brightColorBlurSceneSampler;

layout (binding = 2, rgba16f) uniform writeonly image2D hdrBloomSceneSampler;


void main()
{          
	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);

    vec3 hdrColor = imageLoad(sceneSampler, computeCoord).rgb;    
    vec3 bloomColor = imageLoad(brightColorBlurSceneSampler, computeCoord).rgb;
	
	// additive blending
    hdrColor += bloomColor;
	
    imageStore(hdrBloomSceneSampler, computeCoord, vec4(hdrColor, 1.0));
} 