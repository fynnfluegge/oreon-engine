#version 450
#extension GL_ARB_separate_shader_objects : enable
#extension GL_ARB_shading_language_420pack : enable

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 alpha_out;
layout(location = 2) out vec4 lightScattering_out;

layout (set = 1, binding = 0) uniform sampler2D sunTexture;
layout (set = 1, binding = 1) uniform sampler2D sunTexture_small;

void main()
{	
	vec4 color = texture(sunTexture, gl_PointCoord);
	albedo_out = color;
	alpha_out = vec4(color.a,0.0,0.0,1.0);
	vec4 lightScattering = texture(sunTexture_small, gl_PointCoord);
	lightScattering_out = lightScattering;
}