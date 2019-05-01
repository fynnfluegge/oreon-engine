#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba16f) uniform readonly image2D lightScatteringMask_in;

layout (binding = 1, rgba16f) uniform writeonly image2D lightScatteringMask_out;

uniform mat4 viewProjectionMatrix;
uniform float windowWidth;
uniform float windowHeight;
uniform vec3 sunWorldPosition;

const int NUM_SAMPLES = 200;
const float density = 1.2;
const float decay = 0.97;
const float exposure = 0.2;
const float weight = 0.8;

void main(void){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.xy);
	
	vec4 sunClipSpacePos = viewProjectionMatrix * vec4(sunWorldPosition,1.0);
	vec3 sunNdcSpacePos = sunClipSpacePos.xyz / sunClipSpacePos.w;
	vec2 sunWindowSpacePos = ((sunNdcSpacePos.xy + 1.0) / 2.0) * vec2(windowWidth,windowHeight);
	
	vec2 pixelToSun = computeCoord - sunWindowSpacePos;
	
	vec2 deltaCoord = pixelToSun;
	deltaCoord *= 1.0 /  float(NUM_SAMPLES) * density;
	
	vec2 coord = vec2(computeCoord.x, computeCoord.y);

	float illuminationDecay = 1.0;

	vec3 finalColor = vec3(0,0,0);
	
	float scatteringPreventionFlag = imageLoad(lightScatteringMask_in, computeCoord).a;
	
	// 1 to prevent scattering on atmosphere
	if (scatteringPreventionFlag == 1.0){
		for(int i=0; i < NUM_SAMPLES ; i++)
		{
			vec3 color = imageLoad(lightScatteringMask_in, ivec2(coord.x,coord.y)).rgb;
			coord -= deltaCoord;
			color *= illuminationDecay * weight;
			finalColor += color;
			illuminationDecay *= decay;
		}
		finalColor *= exposure;
	}
	
	imageStore(lightScatteringMask_out, computeCoord, vec4(finalColor, 1.0));
}