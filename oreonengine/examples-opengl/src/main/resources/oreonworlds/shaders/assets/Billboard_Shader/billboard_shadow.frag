#version 330

in vec2 texCoord_FS;

struct Material
{
	sampler2D diffusemap;
};

uniform Material material;
uniform float alphaDiscardThreshold = 0.5;

void main()
{
	float alpha = texture(material.diffusemap, texCoord_FS).a;
	if (alpha < alphaDiscardThreshold)
		discard;
	gl_FragColor = vec4(1,1,1,1);
}