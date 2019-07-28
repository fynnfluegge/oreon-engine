#version 330

in vec3 position_FS;
in vec2 texCoord_FS;

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
uniform float alphaDiscardThreshold = 0.5;
uniform int range = 700;

void main()
{
	float dist = length(eyePosition - position_FS);
	float alpha = texture(material.diffusemap, texCoord_FS).a;
	if (alpha < alphaDiscardThreshold || dist < range)
		discard;
	gl_FragColor = vec4(1,1,1,1);
}