#version 430

in vec3 worldPosition;

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 worldPosition_out;
layout(location = 2) out vec4 normal_out;
layout(location = 3) out vec4 specularEmission_out;
layout(location = 4) out vec4 lightScattering_out;

const vec3 baseColor = vec3(0.18,0.27,0.47);

void main()
{
	float red = -0.00022*(abs(worldPosition.y)-2800) + 0.18;
	float green = -0.00025*(abs(worldPosition.y)-2800) + 0.27;
	float blue = -0.00019*(abs(worldPosition.y)-2800) + 0.47;
	
	albedo_out = vec4(red,green,blue,1);
	worldPosition_out = vec4(0.0,0.0,0.0,1.0);
	normal_out = vec4(0.0,0.0,0.0,1.0);
	specularEmission_out = vec4(0,0,0,1.0);
	lightScattering_out = vec4(0,0,0,0);
}