#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba16f) uniform readonly image2D sceneSampler;

layout (binding = 1, rgba16f) uniform readonly image2D brightColorBlurSceneSampler;

layout (binding = 2, rgba8) uniform writeonly image2D hdrBloomSceneSampler;


const float exposure = 1.0;
const float gamma = 2.2;

void main()
{          
	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);

    vec3 hdrColor = imageLoad(sceneSampler, computeCoord).rgb;    
    vec3 bloomColor = imageLoad(brightColorBlurSceneSampler, computeCoord).rgb;
	
	// additive blending
    hdrColor += bloomColor;
	
    // tone mapping
    vec3 result = vec3(1.0) - exp(-hdrColor * exposure);
	
    // gamma correction    
    result = pow(result, vec3(1.0 / gamma));
	
    imageStore(hdrBloomSceneSampler, computeCoord, vec4(result, 1.0));
} 