#version 330

layout(location = 0) out vec4 albedoSampler;
layout(location = 1) out vec4 worldPositionSampler;
layout(location = 2) out vec4 normalSampler;
layout(location = 3) out vec4 specularEmissionSampler;

void main()
{
	albedoSampler = vec4(0.1,1.0,0.1,1.0);
	normalSampler = vec4(0,0,0,0);
}