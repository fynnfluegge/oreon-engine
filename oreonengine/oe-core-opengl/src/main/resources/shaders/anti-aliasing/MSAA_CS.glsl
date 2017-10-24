#version 430 core

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba8) uniform writeonly image2D coverageMaskTexture;

layout (binding = 1, rgba8) uniform readonly image2DMS multisampleTexture;

void main()
{
	float depth = texture(depthmap, texCoord1).r;
	gl_FragDepth = depth;
	fragColor = texture(texture, texCoord1);
}