#version 430

in vec3 worldPosition;

layout(location = 0) out vec4 outputColor;
layout(location = 1) out vec4 worldPositionSampler;
layout(location = 2) out vec4 normalSampler;
layout(location = 3) out vec4 specularEmissionSampler;

const vec3 baseColor = vec3(0.18,0.27,0.47);

void main()
{
	float red = -0.00022*(abs(worldPosition.y)-2800) + 0.18;
	float green = -0.00025*(abs(worldPosition.y)-2800) + 0.27;
	float blue = -0.00019*(abs(worldPosition.y)-2800) + 0.47;
	
	outputColor = vec4(red,green,blue,1);
	normalSampler = vec4(0.0,0.0,0.0,0.0);
}