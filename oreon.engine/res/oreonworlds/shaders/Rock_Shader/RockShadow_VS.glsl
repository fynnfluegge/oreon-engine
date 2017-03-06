#version 430

layout (location = 0) in vec3 position0;

out int instanceID_GS;

void main()
{
	gl_Position = vec4(position0,1);
	instanceID_GS = gl_InstanceID;
}