#version 330

in vec2 texCoord_FS;

struct Material
{
	sampler2D diffusemap;
};

uniform Material material;

void main()
{
	float alpha = texture(material.diffusemap, texCoord_FS).a;
	if (alpha < 0.6)
		gl_FragDepth = 1;
	gl_FragColor = vec4(0.1,0.9,0.1,1.0);
}