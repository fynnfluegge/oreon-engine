#version 430 core

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba32f) uniform writeonly image2D fractalmap;

struct Fractal
{
	sampler2D heightmap;
	int scaling;
	float strength;
};

uniform Fractal fractals[8];
uniform int N;

void main(void)
{
	ivec2 x = ivec2(gl_GlobalInvocationID.xy);
	vec2 texCoord = gl_GlobalInvocationID.xy/float(N);
	
	float height = 0;
	
	for (int i=0; i<8; i++){
		height += texture(fractals[i].heightmap, texCoord*fractals[i].scaling).r * fractals[i].strength;
	}
	
	imageStore(fractalmap, x, vec4(height,height,height,1));
}