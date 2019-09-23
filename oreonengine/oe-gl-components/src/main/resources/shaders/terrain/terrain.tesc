#version 450
#extension GL_ARB_separate_shader_objects : enable

#lib.glsl

layout(vertices = 16) out;

layout (location = 0) in vec2 inUV[];

layout (location = 0) out vec2 outUV[];

layout (std430, binding = 1) buffer ssbo0 {
	vec3 fogColor;
	float sightRangeFactor;
	int diamond_square_enable;
	int tessFactor;
	float tessSlope;
	float tessShift;
	float xzScale;
	int isBezier;
	float uvScale;
	int largeDetailRange;
};

const int AB = 2;
const int BC = 3;
const int CD = 0;
const int DA = 1;
		
void main()
{
	if(gl_InvocationID == 0)
	{
		vec3 abMid = vec3((gl_in[0].gl_Position.x + gl_in[3].gl_Position.x)/2, 
						  (gl_in[0].gl_Position.y + gl_in[3].gl_Position.y)/2, 
						  (gl_in[0].gl_Position.z + gl_in[3].gl_Position.z)/2);
		vec3 bcMid = vec3((gl_in[3].gl_Position.x + gl_in[15].gl_Position.x)/2,
						  (gl_in[3].gl_Position.y + gl_in[15].gl_Position.y)/2,
						  (gl_in[3].gl_Position.z + gl_in[15].gl_Position.z)/2);
		vec3 cdMid = vec3((gl_in[15].gl_Position.x + gl_in[12].gl_Position.x)/2,
						  (gl_in[15].gl_Position.y + gl_in[12].gl_Position.y)/2,
						  (gl_in[15].gl_Position.z + gl_in[12].gl_Position.z)/2);
		vec3 daMid = vec3((gl_in[12].gl_Position.x + gl_in[0].gl_Position.x)/2, 
						  (gl_in[12].gl_Position.y + gl_in[0].gl_Position.y)/2, 
						  (gl_in[12].gl_Position.z + gl_in[0].gl_Position.z)/2);

		float distanceAB = distance(abMid, camera.eyePosition);
		float distanceBC = distance(bcMid, camera.eyePosition);
		float distanceCD = distance(cdMid, camera.eyePosition);
		float distanceDA = distance(daMid, camera.eyePosition);
		
		gl_TessLevelOuter[AB] = mix(1, gl_MaxTessGenLevel, getLodfactor(distanceAB, tessFactor, tessSlope, tessShift));
		gl_TessLevelOuter[BC] = mix(1, gl_MaxTessGenLevel, getLodfactor(distanceAB, tessFactor, tessSlope, tessShift));
		gl_TessLevelOuter[CD] = mix(1, gl_MaxTessGenLevel, getLodfactor(distanceAB, tessFactor, tessSlope, tessShift));
		gl_TessLevelOuter[DA] = mix(1, gl_MaxTessGenLevel, getLodfactor(distanceAB, tessFactor, tessSlope, tessShift));

		gl_TessLevelInner[0] = (gl_TessLevelOuter[BC] + gl_TessLevelOuter[DA])/4;
		gl_TessLevelInner[1] = (gl_TessLevelOuter[AB] + gl_TessLevelOuter[CD])/4;	
	}
	
	gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;
	outUV[gl_InvocationID] = inUV[gl_InvocationID];
}