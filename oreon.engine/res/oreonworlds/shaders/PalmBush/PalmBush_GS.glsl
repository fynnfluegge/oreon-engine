#version 430

layout(triangles, invocations = 2) in;

layout(triangle_strip, max_vertices = 3) out;

in vec2 texCoord1[];
in vec3 normal1[];
in vec3 position1[];

out vec2 texCoord2;
out vec3 position2;
out vec3 normal2;

layout (std140, row_major) uniform InstancedMatrices{
	mat4 m_World[2];
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 viewProjectionMatrix;
	vec4 frustumPlanes[6];
};

uniform vec4 clipplane;

void main()
{	
	for (int i = 0; i < gl_in.length(); ++i)
	{
		gl_Position = viewProjectionMatrix * (m_World[gl_InvocationID] * gl_in[i].gl_Position);
		gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
		gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
		gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
		gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
		gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
		gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
		texCoord2 = texCoord1[i];
		position2 = m_World[gl_InvocationID] * position1[i];
		normal2 = normal1[i];
		EmitVertex();
	}	
	EndPrimitive();
}