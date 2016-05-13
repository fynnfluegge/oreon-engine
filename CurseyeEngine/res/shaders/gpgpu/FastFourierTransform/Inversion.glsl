#version 430 core

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba32f) uniform image2D displacement;

layout (binding = 1, rgba32f) uniform image2D pingpong0;

layout (binding = 2, rgba32f) uniform image2D pingpong1;

uniform int pingpong;
uniform int N;

void main(void)
{
	ivec2 x = ivec2(gl_GlobalInvocationID.xy);
	
	if(pingpong == 0)
	{
		float h = imageLoad(pingpong0, x).r;
		imageStore(displacement, x, vec4((h/float(N*N)), (h/float(N*N)), (h/float(N*N)), 1));
	}
	else if(pingpong == 1)
	{
		float h = imageLoad(pingpong1, x).r;
		imageStore(displacement, x, vec4((h/float(N*N)), (h/float(N*N)), (h/float(N*N)), 1));
	}
}

