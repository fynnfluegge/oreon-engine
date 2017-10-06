#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 velocity;
layout (location = 2) in float alive;
layout (location = 3) in float size;

out vec3 position0;
out vec3 velocity0;
out float alive0;
out float size0;



void main()
{
	position0 = position;
	velocity0 = velocity;
	alive0 = alive;
	size0 = size;
}