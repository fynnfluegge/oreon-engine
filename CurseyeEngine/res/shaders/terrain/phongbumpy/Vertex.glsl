#version 430

layout (location = 0) in vec3 position0;

uniform sampler2D heightmap;
uniform mat4 worldMatrix;

void main()
{
		vec2 heightmapcoord = position0.xz;
        float height = texture(heightmap, heightmapcoord).r;
		gl_Position = worldMatrix * vec4(position0.x,height,position0.z,1);
}