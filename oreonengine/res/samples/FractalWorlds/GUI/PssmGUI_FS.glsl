#version 330

in vec2 texCoord1;

uniform sampler2DArray tex;
uniform float split;

void main()
{
	float depth = texture(tex, vec3(texCoord1, split)).r;
	gl_FragColor = vec4(depth,depth,depth,1);
}



