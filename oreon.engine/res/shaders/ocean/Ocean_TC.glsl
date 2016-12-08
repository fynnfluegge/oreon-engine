#version 430

layout(vertices = 16) out;

in vec2 texCoord_TC[];

out vec2 texCoord_TE[];

uniform vec3 eyePosition;
uniform int tessFactor;
uniform float tessSlope;
uniform float tessShift;

const int AB = 2;
const int BD = 3;
const int CD = 0;
const int AC = 1;


float LODfactor(float distance)
{
	float tessLevel = max(0.0,tessFactor/(pow(distance, tessSlope)) - tessShift);
	return tessLevel;
}

		
void main(){

	if(gl_InvocationID == 0)
	{
			// C -- D
			// |	|
			// A -- B
			
			
			vec3 abMid = vec3((gl_in[0].gl_Position.x + gl_in[3].gl_Position.x)/2, (gl_in[0].gl_Position.y + gl_in[3].gl_Position.y)/2, (gl_in[0].gl_Position.z + gl_in[3].gl_Position.z)/2);
			vec3 bdMid = vec3((gl_in[3].gl_Position.x + gl_in[15].gl_Position.x)/2, (gl_in[3].gl_Position.y + gl_in[15].gl_Position.y)/2, (gl_in[3].gl_Position.z + gl_in[15].gl_Position.z)/2);
			vec3 cdMid = vec3((gl_in[15].gl_Position.x + gl_in[12].gl_Position.x)/2, (gl_in[15].gl_Position.y + gl_in[12].gl_Position.y)/2, (gl_in[15].gl_Position.z + gl_in[12].gl_Position.z)/2);
			vec3 acMid = vec3((gl_in[12].gl_Position.x + gl_in[0].gl_Position.x)/2, (gl_in[12].gl_Position.y + gl_in[0].gl_Position.y)/2, (gl_in[12].gl_Position.z + gl_in[0].gl_Position.z)/2);
		
	
			float distanceAB = distance(abMid, eyePosition);
			float distanceBD = distance(bdMid, eyePosition);
			float distanceCD = distance(cdMid, eyePosition);
			float distanceAC = distance(acMid, eyePosition);
			
			gl_TessLevelOuter[AB] = mix(1, gl_MaxTessGenLevel, LODfactor(distanceAB));
			gl_TessLevelOuter[BD] = mix(1, gl_MaxTessGenLevel, LODfactor(distanceBD));
			gl_TessLevelOuter[CD] = mix(1, gl_MaxTessGenLevel, LODfactor(distanceCD));
			gl_TessLevelOuter[AC] = mix(1, gl_MaxTessGenLevel, LODfactor(distanceAC));
	
			gl_TessLevelInner[0] = (max(gl_TessLevelOuter[BD],gl_TessLevelOuter[AC]))/2;
			gl_TessLevelInner[1] = (max(gl_TessLevelOuter[AB],gl_TessLevelOuter[CD]))/2;
	}
	
	gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;
	texCoord_TE[gl_InvocationID] = texCoord_TC[gl_InvocationID];
}
