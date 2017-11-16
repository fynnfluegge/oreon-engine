#version 430 core

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, r32f) uniform writeonly image2D sampleCoverageMaskImage;

layout (binding = 1, rgba32f) uniform readonly image2DMS worldPositionImage;

layout (binding = 2, rgba32f) uniform readonly image2DMS normalImage;

uniform sampler2DMS depthmap;
uniform int multisamples;

const float depthThreshold = 0.00001;
const float normalThreshold = 0.2;
const float positionThreshold = 20;

void main()
{
	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
	float threshold = 0;
	
	// detect normal discontinuities
	float normalDiscontinuities = 0;
	vec3[8] normals; 
	for (int i=0; i<multisamples; i++){
		normals[i] = imageLoad(normalImage, computeCoord,i).rgb; 
	}
	for (int i=0; i<multisamples-1; i++){
		normalDiscontinuities += distance(normals[i],normals[i+1]);
	}
	
	// detect world position discontinuities
	float positionDiscontinuities = 0;
	vec3[8] positions; 
	for (int i=0; i<multisamples; i++){
		positions[i] = imageLoad(worldPositionImage, computeCoord,i).rgb; 
	}
	for (int i=0; i<multisamples-1; i++){
		positionDiscontinuities += distance(positions[i],positions[i+1]);
	}
	
	// depth discontinuities
	float depthDiscontinuities = 0;
	float[8] depths;
	for (int i=0; i<multisamples; i++){
		depths[i] = texelFetch(depthmap, computeCoord, i).r;
	}
	for (int i=0; i<multisamples-1; i++){
		depthDiscontinuities += abs(depths[i] - depths[i+1]);
	}
		
	float coverageValue = 0;
	// if ((depthDiscontinuities > depthThreshold)){
		// || 
	    // (normalDiscontinuities > normalThreshold) || 
	if(positionDiscontinuities > positionThreshold){
		
		coverageValue = 1.0;
	}
			  
	imageStore(sampleCoverageMaskImage, computeCoord, vec4(coverageValue,0,0,1));
}