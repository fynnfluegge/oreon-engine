#version 430

layout(vertices = 16) out;

in vec2 texCoord1[];

out vec2 texCoord2[];

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

uniform int tessFactor;
uniform float tessSlope;
uniform float tessShift;

const int AB = 2;
const int BC = 3;
const int CD = 0;
const int DA = 1;

float LODfactor(float distance){
	
	float tessLevel = max(0.0,tessFactor/(pow(distance, tessSlope)) - tessShift);

	return tessLevel;
}
		
void main()
{

	if(gl_InvocationID == 0)
	{
			vec3 abMid = vec3((gl_in[0].gl_Position.x + gl_in[3].gl_Position.x)/2, (gl_in[0].gl_Position.y + gl_in[3].gl_Position.y)/2, (gl_in[0].gl_Position.z + gl_in[3].gl_Position.z)/2);
			vec3 bcMid = vec3((gl_in[3].gl_Position.x + gl_in[15].gl_Position.x)/2, (gl_in[3].gl_Position.y + gl_in[15].gl_Position.y)/2, (gl_in[3].gl_Position.z + gl_in[15].gl_Position.z)/2);
			vec3 cdMid = vec3((gl_in[15].gl_Position.x + gl_in[12].gl_Position.x)/2, (gl_in[15].gl_Position.y + gl_in[12].gl_Position.y)/2, (gl_in[15].gl_Position.z + gl_in[12].gl_Position.z)/2);
			vec3 daMid = vec3((gl_in[12].gl_Position.x + gl_in[0].gl_Position.x)/2, (gl_in[12].gl_Position.y + gl_in[0].gl_Position.y)/2, (gl_in[12].gl_Position.z + gl_in[0].gl_Position.z)/2);
	
			float distanceAB = distance(abMid, eyePosition);
			float distanceBC = distance(bcMid, eyePosition);
			float distanceCD = distance(cdMid, eyePosition);
			float distanceDA = distance(daMid, eyePosition);
			
			gl_TessLevelOuter[AB] = mix(1, gl_MaxTessGenLevel, LODfactor(distanceAB));
			gl_TessLevelOuter[BC] = mix(1, gl_MaxTessGenLevel, LODfactor(distanceBC));
			gl_TessLevelOuter[CD] = mix(1, gl_MaxTessGenLevel, LODfactor(distanceCD));
			gl_TessLevelOuter[DA] = mix(1, gl_MaxTessGenLevel, LODfactor(distanceDA));
	
			gl_TessLevelInner[0] = (gl_TessLevelOuter[BC] + gl_TessLevelOuter[DA])/4;
			gl_TessLevelInner[1] = (gl_TessLevelOuter[AB] + gl_TessLevelOuter[CD])/4;	
	}
	
	gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;
	texCoord2[gl_InvocationID] = texCoord1[gl_InvocationID];
}