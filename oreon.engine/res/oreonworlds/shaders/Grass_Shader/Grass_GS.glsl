#version 430

layout(triangles, invocations = 1) in;

layout(triangle_strip, max_vertices = 3) out;

in int instanceID_GS[];
in vec2 texCoord_GS[];
in vec3 normal_GS[];

out vec3 position_FS;
out vec2 texCoord_FS;
out vec3 normal_FS;
out vec4 viewSpacePos;

layout (std140, row_major) uniform worldMatrices{
	mat4 m_World[500];
};

layout (std140, row_major) uniform modelMatrices{
	mat4 m_Model[500];
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 viewProjectionMatrix;
	vec4 frustumPlanes[6];
};

uniform vec4 clipplane;
uniform int matrixIndices[500];

void main()
{	
	for (int i = 0; i < gl_in.length(); ++i)
	{
		vec4 worldPos = m_World[matrixIndices[instanceID_GS[i]]] * gl_in[i].gl_Position;
		gl_Position = viewProjectionMatrix * worldPos;
		gl_ClipDistance[0] = dot(gl_Position,frustumPlanes[0]);
		gl_ClipDistance[1] = dot(gl_Position,frustumPlanes[1]);
		gl_ClipDistance[2] = dot(gl_Position,frustumPlanes[2]);
		gl_ClipDistance[3] = dot(gl_Position,frustumPlanes[3]);
		gl_ClipDistance[4] = dot(gl_Position,frustumPlanes[4]);
		gl_ClipDistance[5] = dot(gl_Position,frustumPlanes[5]);
		gl_ClipDistance[6] = dot(gl_Position,clipplane);
		texCoord_FS = texCoord_GS[i];
		position_FS = worldPos.xyz;
		normal_FS = (m_Model[matrixIndices[instanceID_GS[i]]] * vec4(normal_GS[i],1)).xyz;
		viewSpacePos = m_View * worldPos;
		EmitVertex();
	}	
	EndPrimitive();
}