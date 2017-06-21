#version 330

layout(points) in;
layout(points) out;
layout(max_vertices = 1) out;

in vec3 position0[];
in vec3 velocity0[];
in float alive0[];
in float size0[];

out vec3 position1;
out vec3 velocity1;
out float alive1;
out float size1;

uniform int clear;


#define PARTICLE_LIFETIME 5000


void main()
{
	if (clear == 0)
	{
		if (alive0[0] < PARTICLE_LIFETIME)
		{
			position1 = position0[0] + (velocity0[0]);
			velocity1 = velocity0[0];
			alive1 = alive0[0] + 10;
			size1 = size0[0];
			EmitVertex();
			EndPrimitive();
		}
		else{
			position1 = vec3(0,0,0);
			velocity1 = velocity0[0];
			alive1 = 0;
			size1 = size0[0];
			EmitVertex();
			EndPrimitive();
		}
	}
	else
	{
		if (alive0[0] < PARTICLE_LIFETIME)
		{
			position1 = position0[0] + (velocity0[0]);
			velocity1 = velocity0[0];
			alive1 = alive0[0] + 10;
			size1 = size0[0] - 10;
			EmitVertex();
			EndPrimitive();
		}
		if (alive0[0] >= PARTICLE_LIFETIME)
		{
			size1 = 0;
			EmitVertex();
			EndPrimitive();
		}
	}
}