#version 430 core

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba16f) uniform writeonly image2D fxaaScene_out;

layout (binding = 1, rgba16f) uniform readonly image2D sceneImage_in;

uniform vec2 u_texelStep = vec2(128.0f,128.0f);
uniform float u_lumaThreshold = 0.5f;
uniform float u_mulReduce = 8.0f;
uniform float u_minReduce = 128.0f;
uniform float u_maxSpan = 8.0f;
uniform int u_showEdges = 0;
uniform int width;
uniform int height;

uniform sampler2D sceneTexture;

const float EDGE_THRESHOLD_MIN = 0.0312;
const float EDGE_THRESHOLD_MAX = 0.125;
const int ITERATIONS = 7;
const float QUALITY[7] = {1.5f, 2.0f, 2.0f, 2.0f, 2.0f, 4.0f, 8.0f};
const float SUBPIXEL_QUALITY = 0.75;

float rgb2luma(vec3 rgb){
    return sqrt(dot(rgb, vec3(0.299 , 0.587 , 0.114)));
}

void main(){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
	
	vec2 uv = vec2(gl_GlobalInvocationID.x/float(width), gl_GlobalInvocationID.y/float(height));
	
	vec2 inverseScreenSize = vec2(1.0/float(width), 1.0/float(height));
	
	vec3 rgb = imageLoad(sceneImage_in, computeCoord).rgb;
	
	// Luma at the current fragment
	float lumaCenter = rgb2luma(rgb);

	// Luma at the four direct neighbours of the current fragment.
	float lumaDown = rgb2luma(imageLoad(sceneImage_in,computeCoord + ivec2(0,-1)).rgb);
	float lumaUp = rgb2luma(imageLoad(sceneImage_in,computeCoord + ivec2(0,1)).rgb);
	float lumaLeft = rgb2luma(imageLoad(sceneImage_in,computeCoord + ivec2(-1,0)).rgb);
	float lumaRight = rgb2luma(imageLoad(sceneImage_in,computeCoord + ivec2(1,0)).rgb);

	// Find the maximum and minimum luma around the current fragment.
	float lumaMin = min(lumaCenter,min(min(lumaDown,lumaUp),min(lumaLeft,lumaRight)));
	float lumaMax = max(lumaCenter,max(max(lumaDown,lumaUp),max(lumaLeft,lumaRight)));

	// Compute the delta.
	float lumaRange = lumaMax - lumaMin;

	// If the luma variation is lower that a threshold (or if we are in a really dark area), we are not on an edge, don't perform any AA.
	if(lumaRange < max(EDGE_THRESHOLD_MIN,lumaMax*EDGE_THRESHOLD_MAX)){
		imageStore(fxaaScene_out, computeCoord, vec4(rgb, 1.0));
		return;
	}
	
	// Query the 4 remaining corners lumas.
	float lumaDownLeft = rgb2luma(imageLoad(sceneImage_in,computeCoord + ivec2(-1,-1)).rgb);
	float lumaUpRight = rgb2luma(imageLoad(sceneImage_in,computeCoord + ivec2(1,1)).rgb);
	float lumaUpLeft = rgb2luma(imageLoad(sceneImage_in,computeCoord + ivec2(-1,1)).rgb);
	float lumaDownRight = rgb2luma(imageLoad(sceneImage_in,computeCoord + ivec2(1,-1)).rgb);

	// Combine the four edges lumas (using intermediary variables for future computations with the same values).
	float lumaDownUp = lumaDown + lumaUp;
	float lumaLeftRight = lumaLeft + lumaRight;

	// Same for corners
	float lumaLeftCorners = lumaDownLeft + lumaUpLeft;
	float lumaDownCorners = lumaDownLeft + lumaDownRight;
	float lumaRightCorners = lumaDownRight + lumaUpRight;
	float lumaUpCorners = lumaUpRight + lumaUpLeft;

	// Compute an estimation of the gradient along the horizontal and vertical axis.
	float edgeHorizontal =  abs(-2.0 * lumaLeft + lumaLeftCorners)  + abs(-2.0 * lumaCenter + lumaDownUp ) * 2.0    + abs(-2.0 * lumaRight + lumaRightCorners);
	float edgeVertical =    abs(-2.0 * lumaUp + lumaUpCorners)      + abs(-2.0 * lumaCenter + lumaLeftRight) * 2.0  + abs(-2.0 * lumaDown + lumaDownCorners);

	// Is the local edge horizontal or vertical ?
	bool isHorizontal = (edgeHorizontal >= edgeVertical);
	
	// Select the two neighboring texels lumas in the opposite direction to the local edge.
	float luma1 = isHorizontal ? lumaDown : lumaLeft;
	float luma2 = isHorizontal ? lumaUp : lumaRight;
	
	// Compute gradients in this direction.
	float gradient1 = luma1 - lumaCenter;
	float gradient2 = luma2 - lumaCenter;

	// Which direction is the steepest ?
	bool is1Steepest = abs(gradient1) >= abs(gradient2);

	// Gradient in the corresponding direction, normalized.
	float gradientScaled = 0.25*max(abs(gradient1),abs(gradient2));
	
	float stepLength = isHorizontal ? inverseScreenSize.y : inverseScreenSize.x;

	// Average luma in the correct direction.
	float lumaLocalAverage = 0.0;

	if(is1Steepest){
		// Switch the direction
		stepLength = - stepLength;
		lumaLocalAverage = 0.5*(luma1 + lumaCenter);
	} else {
		lumaLocalAverage = 0.5*(luma2 + lumaCenter);
	}

	// Shift UV in the correct direction by half a pixel.
	vec2 currentUv = uv;
	if(isHorizontal){
		currentUv.y += stepLength * 0.5;
	} else {
		currentUv.x += stepLength * 0.5;
	}
	
	// Compute offset (for each iteration step) in the right direction.
	vec2 offset = isHorizontal ? vec2(inverseScreenSize.x,0.0) : vec2(0.0,inverseScreenSize.y);
	// Compute UVs to explore on each side of the edge, orthogonally. The QUALITY allows us to step faster.
	vec2 uv1 = currentUv - offset;
	vec2 uv2 = currentUv + offset;

	// Read the lumas at both current extremities of the exploration segment, and compute the delta wrt to the local average luma.
	float lumaEnd1 = rgb2luma(texture(sceneTexture, uv1).rgb);
	float lumaEnd2 = rgb2luma(texture(sceneTexture, uv2).rgb);
	lumaEnd1 -= lumaLocalAverage;
	lumaEnd2 -= lumaLocalAverage;

	// If the luma deltas at the current extremities are larger than the local gradient, we have reached the side of the edge.
	bool reached1 = abs(lumaEnd1) >= gradientScaled;
	bool reached2 = abs(lumaEnd2) >= gradientScaled;
	bool reachedBoth = reached1 && reached2;

	// If the side is not reached, we continue to explore in this direction.
	if(!reached1){
		uv1 -= offset;
	}
	if(!reached2){
		uv2 += offset;
	}   
	
	// If both sides have not been reached, continue to explore.
	if(!reachedBoth){

		for(int i = 0; i < ITERATIONS; i++){
			// If needed, read luma in 1st direction, compute delta.
			if(!reached1){
				lumaEnd1 = rgb2luma(texture(sceneTexture, uv1).rgb);
				lumaEnd1 = lumaEnd1 - lumaLocalAverage;
			}
			// If needed, read luma in opposite direction, compute delta.
			if(!reached2){
				lumaEnd2 = rgb2luma(texture(sceneTexture, uv2).rgb);
				lumaEnd2 = lumaEnd2 - lumaLocalAverage;
			}
			// If the luma deltas at the current extremities is larger than the local gradient, we have reached the side of the edge.
			reached1 = abs(lumaEnd1) >= gradientScaled;
			reached2 = abs(lumaEnd2) >= gradientScaled;
			reachedBoth = reached1 && reached2;

			// If the side is not reached, we continue to explore in this direction, with a variable quality.
			if(!reached1){
				uv1 -= offset * QUALITY[i];
			}
			if(!reached2){
				uv2 += offset * QUALITY[i];
			}

			// If both sides have been reached, stop the exploration.
			if(reachedBoth){ break;}
		}
	}
	
	// Compute the distances to each extremity of the edge.
	float distance1 = isHorizontal ? (computeCoord.x - uv1.x) : (computeCoord.y - uv1.y);
	float distance2 = isHorizontal ? (uv2.x - computeCoord.x) : (uv2.y - computeCoord.y);

	// In which direction is the extremity of the edge closer ?
	bool isDirection1 = distance1 < distance2;
	float distanceFinal = min(distance1, distance2);

	// Length of the edge.
	float edgeThickness = (distance1 + distance2);

	// UV offset: read in the direction of the closest side of the edge.
	float pixelOffset = - distanceFinal / edgeThickness + 0.5;
	
	// Is the luma at center smaller than the local average ?
	bool isLumaCenterSmaller = lumaCenter < lumaLocalAverage;

	// If the luma at center is smaller than at its neighbour, the delta luma at each end should be positive (same variation).
	// (in the direction of the closer side of the edge.)
	bool correctVariation = ((isDirection1 ? lumaEnd1 : lumaEnd2) < 0.0) != isLumaCenterSmaller;

	// If the luma variation is incorrect, do not offset.
	float finalOffset = correctVariation ? pixelOffset : 0.0;
	
	// Sub-pixel shifting
	// Full weighted average of the luma over the 3x3 neighborhood.
	float lumaAverage = (1.0/12.0) * (2.0 * (lumaDownUp + lumaLeftRight) + lumaLeftCorners + lumaRightCorners);
	// Ratio of the delta between the global average and the center luma, over the luma range in the 3x3 neighborhood.
	float subPixelOffset1 = clamp(abs(lumaAverage - lumaCenter)/lumaRange,0.0,1.0);
	float subPixelOffset2 = (-2.0 * subPixelOffset1 + 3.0) * subPixelOffset1 * subPixelOffset1;
	// Compute a sub-pixel offset based on this delta.
	float subPixelOffsetFinal = subPixelOffset2 * subPixelOffset2 * SUBPIXEL_QUALITY;

	// Pick the biggest of the two offsets.
	finalOffset = max(finalOffset,subPixelOffsetFinal);
	
	// Compute the final UV coordinates.
	vec2 finalUv = uv;
	if(isHorizontal){
		finalUv.y += int(finalOffset * stepLength);
	} else {
		finalUv.x += int(finalOffset * stepLength);
	}

	// Read the color at the new UV coordinates, and use it.
	vec3 finalColor = texture(sceneTexture, finalUv).rgb;
	
	imageStore(fxaaScene_out, computeCoord, vec4(finalColor, 1.0));	
}

// alternative approach
// void main(){

	// Sampling neighbour texels. Offsets are adapted to OpenGL texture coordinates. 
	// vec3 rgbNW = imageLoad(sceneImage_in, computeCoord + ivec2(-1,1)).rgb;
    // vec3 rgbNE = imageLoad(sceneImage_in, computeCoord + ivec2(1,1)).rgb;
    // vec3 rgbSW = imageLoad(sceneImage_in, computeCoord + ivec2(-1,-1)).rgb;
    // vec3 rgbSE = imageLoad(sceneImage_in, computeCoord + ivec2(1,-1)).rgb;

	// const vec3 toLuma = vec3(0.299, 0.587, 0.114);
	
	// Convert from RGB to luma.
	// float lumaNW = dot(rgbNW, toLuma);
	// float lumaNE = dot(rgbNE, toLuma);
	// float lumaSW = dot(rgbSW, toLuma);
	// float lumaSE = dot(rgbSE, toLuma);
	// float lumaM = dot(rgb, toLuma);

	// Gather minimum and maximum luma.
	// float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
	// float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));
	
	// If contrast is lower than a maximum threshold ...
	// if (lumaMax - lumaMin < lumaMax * u_lumaThreshold)
	// {
		// ... do no AA and return.
		// imageStore(fxaaScene_out, computeCoord, vec4(rgbM, 1.0));
		
		// return;
	// }  
	
	// Sampling is done along the gradient.
	// vec2 samplingDirection;	
	// samplingDirection.x = -((lumaNW + lumaNE) - (lumaSW + lumaSE));
    // samplingDirection.y =  ((lumaNW + lumaSW) - (lumaNE + lumaSE));
    
    // Sampling step distance depends on the luma: The brighter the sampled texels, the smaller the final sampling step direction.
    // This results, that brighter areas are less blurred/more sharper than dark areas.  
    // float samplingDirectionReduce = max((lumaNW + lumaNE + lumaSW + lumaSE) * 0.25 * u_mulReduce, u_minReduce);

	// Factor for norming the sampling direction plus adding the brightness influence. 
	// float minSamplingDirectionFactor = 1.0 / (min(abs(samplingDirection.x), abs(samplingDirection.y)) + samplingDirectionReduce);
    
    // Calculate final sampling direction vector by reducing, clamping to a range and finally adapting to the texture size. 
    // samplingDirection = clamp(samplingDirection * minSamplingDirectionFactor, vec2(-u_maxSpan, -u_maxSpan), vec2(u_maxSpan, u_maxSpan)) * u_texelStep;
	
	// Inner samples on the tab.
	// vec3 rgbSampleNeg = imageLoad(sceneImage_in, computeCoord + ivec2(samplingDirection * (1.0/3.0 - 0.5)) * ivec2(1280,720)).rgb;
	// vec3 rgbSamplePos = imageLoad(sceneImage_in, computeCoord + ivec2(samplingDirection * (2.0/3.0 - 0.5)) * ivec2(1280,720)).rgb;

	// vec3 rgbTwoTab = (rgbSamplePos + rgbSampleNeg) * 0.5;  

	// Outer samples on the tab.
	// vec3 rgbSampleNegOuter = imageLoad(sceneImage_in, computeCoord + ivec2(samplingDirection * (0.0/3.0 - 0.5)) * ivec2(1280,720)).rgb;
	// vec3 rgbSamplePosOuter = imageLoad(sceneImage_in, computeCoord + ivec2(samplingDirection * (3.0/3.0 - 0.5)) * ivec2(1280,720)).rgb;
	
	// vec3 rgbFourTab = (rgbSamplePosOuter + rgbSampleNegOuter) * 0.25 + rgbTwoTab * 0.5;   
	
	// Calculate luma for checking against the minimum and maximum value.
	// float lumaFourTab = dot(rgbFourTab, toLuma);
	
	// Are outer samples of the tab beyond the edge ... 
	// if (lumaFourTab < lumaMin || lumaFourTab > lumaMax)
	// {
		// ... yes, so use only two samples.
		// imageStore(fxaaScene_out, computeCoord, vec4(rgbTwoTab, 1.0));
	// }
	// else
	// {
		// ... no, so use four samples. 
		// imageStore(fxaaScene_out, computeCoord, vec4(rgbFourTab, 1.0));
	// }

	// Show edges for debug purposes.	
	// if (u_showEdges == 1)
	// {
		// imageStore(fxaaScene_out, computeCoord, vec4(1.0,0.0,0.0,1.0));
	// }
// }