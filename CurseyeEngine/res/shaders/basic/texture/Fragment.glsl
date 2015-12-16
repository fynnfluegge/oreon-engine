#version 330

in vec2 texCoord1;

uniform sampler2D texture;

void main()
{
	gl_FragColor = texture2D(texture, texCoord1);
}



