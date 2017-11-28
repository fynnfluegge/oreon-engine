#version 430 core

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, r32f) uniform writeonly image2D sampleCoverageMaskImage;

layout (binding = 1, rgba32f) uniform readonly image2DMS worldPositionImage;

layout (binding = 2, rgba16f) uniform writeonly image2D lightScatteringMask_out;

layout (binding = 3, rgba16f) uniform readonly image2DMS lightScatteringMask_in;

uniform int multisamples;

const float threshold = 40;

void main()
{
	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
	
	// detect world position discontinuities
	float positionDiscontinuities = 0;
	vec3[8] positions; 
	for (int i=0; i<multisamples; i++){
		positions[i] = imageLoad(worldPositionImage, computeCoord,i).rgb; 
	}
	for (int i=0; i<multisamples-1; i++){
		positionDiscontinuities += distance(positions[i],positions[i+1]);
	}
		
	float coverageValue = 0;
	
	if(positionDiscontinuities > threshold){	
		coverageValue = 1.0;
	}
	
	vec2 lightScatteringMaskValue = vec2(0,0);
	if (coverageValue == 1.0){
		for (int i=0; i<multisamples-1; i++){
			lightScatteringMaskValue += imageLoad(lightScatteringMask_in, computeCoord, i).ra;
		}
		lightScatteringMaskValue /= multisamples;
		
		if (lightScatteringMaskValue.x > 0.5){
			lightScatteringMaskValue.x = 1.0;
		}
		else{
			lightScatteringMaskValue.x = 0.0;
		}
		if (lightScatteringMaskValue.y > 0.5){
			lightScatteringMaskValue.y = 1.0;
		}
		else{
			lightScatteringMaskValue.y = 0.0;
		}
	}
	else{
		lightScatteringMaskValue = imageLoad(lightScatteringMask_in, computeCoord, 0).ra;
	}
			  
	imageStore(sampleCoverageMaskImage, computeCoord, vec4(coverageValue,0,0,1));
	imageStore(lightScatteringMask_out, computeCoord, vec4(lightScatteringMaskValue.x,lightScatteringMaskValue.x,lightScatteringMaskValue.x,lightScatteringMaskValue.y));
}