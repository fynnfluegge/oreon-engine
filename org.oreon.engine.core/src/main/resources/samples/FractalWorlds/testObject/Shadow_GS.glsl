#version 430

layout(triangles, invocations = 4) in;

layout(triangle_strip, max_vertices = 3) out;

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 viewProjectionMatrix;
	vec4 frustumPlanes[6];
};

layout (std140, row_major) uniform LightViewProjections{
	mat4 m_lightViewProjection[4];
};

void main()
{	
	for (int i = 0; i < gl_in.length(); ++i)
	{
		gl_Layer = gl_InvocationID;
		gl_Position = m_lightViewProjection[gl_InvocationID] * gl_in[i].gl_Position;
		gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
		gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
		gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
		gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
		gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
		gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
		EmitVertex();
	}	
	EndPrimitive();
}
