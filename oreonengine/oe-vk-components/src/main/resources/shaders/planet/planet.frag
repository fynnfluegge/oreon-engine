#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec3 inPosition;
layout (location = 1) in vec2 inUV;
layout (location = 2) in vec3 inTangent;

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 worldPosition_out;
layout(location = 2) out vec4 normal_out;
layout(location = 3) out vec4 specularEmission_out;
layout(location = 4) out vec4 lightScattering_out;

const float zfar = 10000;
const float znear = 0.1;
const vec3 fogColor = vec3(0.65,0.85,0.9);

void main()
{		
	albedo_out = vec4(0,1,0,1);
	worldPosition_out = vec4(inPosition,1);
	normal_out = vec4(0,0,0,1);
	specularEmission_out = vec4(1,0,0,1);
	lightScattering_out = vec4(0,0,0,1);
}