#version 430

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 worldPosition_out;
layout(location = 2) out vec4 normal_out;
layout(location = 3) out vec4 specularEmission_out;

uniform sampler2D sunTexture;
uniform sampler2D sunTexture_small;

void main()
{	
	albedo_out = texture2D(sunTexture, gl_PointCoord);
	worldPosition_out = vec4(0.0,0.0,0.0,1.0);
	normal_out = vec4(0.0,0.0,0.0,1.0);
	specularEmission_out = vec4(0,0,0,1.0);
	//outputColorForScattering = texture2D(sunTexture_small, gl_PointCoord);
}