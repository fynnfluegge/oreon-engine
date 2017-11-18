#version 430 core

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba16f) uniform writeonly image2D fxaaScene_out;

layout (binding = 1, rgba16f) uniform readonly image2D sceneImage_in;

uniform vec2 u_texelStep = vec2(1280,720);
uniform float u_lumaThreshold = 0.5f;
uniform float u_mulReduce = 8.0f;
uniform float u_minReduce = 128.0f;
uniform float u_maxSpan = 8.0f;
uniform int u_showEdges = 0;

void main(){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
	
	vec3 rgbM = imageLoad(sceneImage_in, computeCoord).rgb;
	
	// Sampling neighbour texels. Offsets are adapted to OpenGL texture coordinates. 
	vec3 rgbNW = imageLoad(sceneImage_in, computeCoord + ivec2(-1,1)).rgb;
    vec3 rgbNE = imageLoad(sceneImage_in, computeCoord + ivec2(1,1)).rgb;
    vec3 rgbSW = imageLoad(sceneImage_in, computeCoord + ivec2(-1,-1)).rgb;
    vec3 rgbSE = imageLoad(sceneImage_in, computeCoord + ivec2(1,-1)).rgb;

	const vec3 toLuma = vec3(0.299, 0.587, 0.114);
	
	// Convert from RGB to luma.
	float lumaNW = dot(rgbNW, toLuma);
	float lumaNE = dot(rgbNE, toLuma);
	float lumaSW = dot(rgbSW, toLuma);
	float lumaSE = dot(rgbSE, toLuma);
	float lumaM = dot(rgbM, toLuma);

	// Gather minimum and maximum luma.
	float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
	float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));
	
	// If contrast is lower than a maximum threshold ...
	if (lumaMax - lumaMin < lumaMax * u_lumaThreshold)
	{
		// ... do no AA and return.
		imageStore(fxaaScene_out, computeCoord, vec4(rgbM, 1.0));
		
		return;
	}  
	
	// Sampling is done along the gradient.
	vec2 samplingDirection;	
	samplingDirection.x = -((lumaNW + lumaNE) - (lumaSW + lumaSE));
    samplingDirection.y =  ((lumaNW + lumaSW) - (lumaNE + lumaSE));
    
    // Sampling step distance depends on the luma: The brighter the sampled texels, the smaller the final sampling step direction.
    // This results, that brighter areas are less blurred/more sharper than dark areas.  
    float samplingDirectionReduce = max((lumaNW + lumaNE + lumaSW + lumaSE) * 0.25 * u_mulReduce, u_minReduce);

	// Factor for norming the sampling direction plus adding the brightness influence. 
	float minSamplingDirectionFactor = 1.0 / (min(abs(samplingDirection.x), abs(samplingDirection.y)) + samplingDirectionReduce);
    
    // Calculate final sampling direction vector by reducing, clamping to a range and finally adapting to the texture size. 
    samplingDirection = clamp(samplingDirection * minSamplingDirectionFactor, vec2(-u_maxSpan, -u_maxSpan), vec2(u_maxSpan, u_maxSpan)) * u_texelStep;
	
	// Inner samples on the tab.
	vec3 rgbSampleNeg = imageLoad(sceneImage_in, computeCoord + ivec2(samplingDirection * (1.0/3.0 - 0.5))).rgb;
	vec3 rgbSamplePos = imageLoad(sceneImage_in, computeCoord + ivec2(samplingDirection * (2.0/3.0 - 0.5))).rgb;

	vec3 rgbTwoTab = (rgbSamplePos + rgbSampleNeg) * 0.5;  

	// Outer samples on the tab.
	vec3 rgbSampleNegOuter = imageLoad(sceneImage_in, computeCoord + ivec2(samplingDirection * (0.0/3.0 - 0.5))).rgb;
	vec3 rgbSamplePosOuter = imageLoad(sceneImage_in, computeCoord + ivec2(samplingDirection * (3.0/3.0 - 0.5))).rgb;
	
	vec3 rgbFourTab = (rgbSamplePosOuter + rgbSampleNegOuter) * 0.25 + rgbTwoTab * 0.5;   
	
	// Calculate luma for checking against the minimum and maximum value.
	float lumaFourTab = dot(rgbFourTab, toLuma);
	
	// Are outer samples of the tab beyond the edge ... 
	if (lumaFourTab < lumaMin || lumaFourTab > lumaMax)
	{
		// ... yes, so use only two samples.
		imageStore(fxaaScene_out, computeCoord, vec4(rgbTwoTab, 1.0));
	}
	else
	{
		// ... no, so use four samples. 
		imageStore(fxaaScene_out, computeCoord, vec4(rgbFourTab, 1.0));
	}

	//Show edges for debug purposes.	
	if (u_showEdges == 1)
	{
		imageStore(fxaaScene_out, computeCoord, vec4(1.0,0.0,0.0,1.0));
	}
}