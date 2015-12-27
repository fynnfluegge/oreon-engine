#version 430

layout(vertices = 16) out;

flat out int highDetailTE;
flat out int bezierTE;
out int crackAvoidance;

uniform vec3 eyePosition;
uniform int tessFactor;
uniform float tessSlope;
uniform float tessShift;
uniform int detailRange;

const int AB = 2;
const int BC = 3;
const int CD = 0;
const int DA = 1;



float LODfactor(float distance){
	
	float tessLevel = max(0.0,tessFactor/(pow(distance, tessSlope)) - tessShift);
	if (tessLevel > 0){
		crackAvoidance++;
		bezierTE = 1;
	}
		
	if (distance  < detailRange)
		highDetailTE = 1;
		
	return tessLevel;
}



		
void main(){

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
}