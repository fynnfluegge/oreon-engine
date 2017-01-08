#version 430

layout(triangles, invocations = 9) in;

// 6 shadow layers := 6 triangles := 18 vertices
layout(triangle_strip, max_vertices = 18) out;

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 viewProjectionMatrix;
	vec4 frustumPlanes[6];
};

layout (std140, row_major) uniform InstancedMatrices{
	mat4 m_World[9];
};

layout (std140, row_major) uniform LightViewProjections{
	mat4 m_lightViewProjection[6];
};

uniform int pssm_splits;
uniform vec4 clipplane;

void main()
{	
	for (int j = 0; j < pssm_splits; j++){
		for (int i = 0; i < gl_in.length(); ++i)
		{
			gl_Layer = j;
			gl_Position = m_lightViewProjection[j] * m_World[ gl_InvocationID ] * gl_in[i].gl_Position;
			gl_ClipDistance[0] = dot(gl_Position,frustumPlanes[0]);
			gl_ClipDistance[1] = dot(gl_Position,frustumPlanes[1]);
			gl_ClipDistance[2] = dot(gl_Position,frustumPlanes[2]);
			gl_ClipDistance[3] = dot(gl_Position,frustumPlanes[3]);
			gl_ClipDistance[4] = dot(gl_Position,frustumPlanes[4]);
			gl_ClipDistance[5] = dot(gl_Position,frustumPlanes[5]);
			gl_ClipDistance[6] = dot(gl_Position,clipplane);
			EmitVertex();
		}	
		EndPrimitive();
	}
}
