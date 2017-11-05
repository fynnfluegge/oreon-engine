#version 430

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 alpha_out;
layout(location = 4) out vec4 lightScattering_out;


uniform sampler2D sunTexture;
uniform sampler2D sunTexture_small;

void main()
{	
	vec4 color = texture(sunTexture, gl_PointCoord);
	albedo_out = color;
	alpha_out = vec4(color.a,0.0,0.0,1.0);
	vec4 lightScattering = texture(sunTexture_small, gl_PointCoord);
	lightScattering_out = lightScattering;
}