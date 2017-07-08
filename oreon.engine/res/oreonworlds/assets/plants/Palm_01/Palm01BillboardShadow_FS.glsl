#version 330

in vec2 texCoord_FS;
in vec3 position_FS;

struct Material
{
	sampler2D diffusemap;
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 viewProjectionMatrix;
	vec4 frustumPlanes[6];
};

uniform Material material;

float alphaDistanceFactor(float dist)
{
	return 0.04f * (dist-250);
}

void main()
{
	float dist = length(eyePosition - position_FS);
	float alpha = texture(material.diffusemap, texCoord_FS).a;
	if (alpha < 0.2)
		discard;
	alpha *= alphaDistanceFactor(dist);
	gl_FragColor = vec4(0.1,0.9,0.1,alpha);
}