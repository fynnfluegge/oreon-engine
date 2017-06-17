#version 430

layout(location = 0) out vec4 outputColor;
layout(location = 1) out vec4 outputColorForScattering;

uniform sampler2D sunTexture;
uniform sampler2D sunTexture_small;

void main()
{	
	outputColor = texture2D(sunTexture, gl_PointCoord);
	outputColorForScattering = texture2D(sunTexture_small, gl_PointCoord);
}