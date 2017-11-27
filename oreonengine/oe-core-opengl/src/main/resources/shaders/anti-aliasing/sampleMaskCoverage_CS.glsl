#version 430 core

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, r32f) uniform writeonly image2D sampleCoverageMaskImage;

layout (binding = 1, rgba32f) uniform readonly image2DMS worldPositionImage;

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
			  
	imageStore(sampleCoverageMaskImage, computeCoord, vec4(coverageValue,0,0,1));
}