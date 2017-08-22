#version 430

layout(triangles, invocations = 6) in;

layout(triangle_strip, max_vertices = 3) out;

in int instanceID_GS[];
in vec2 texCoord_GS[];

out vec2 texCoord_FS;

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 viewProjectionMatrix;
	vec4 frustumPlanes[6];
};

layout (std140, row_major) uniform InstancedMatrices{
	mat4 m_World[512];
	mat4 m_Model[512];
};

layout (std140, row_major) uniform LightViewProjections{
	mat4 m_lightViewProjection[6];
};

uniform int matrixIndices[100];

void main()
{	
		for (int i = 0; i < gl_in.length(); ++i)
		{
			gl_Layer = gl_InvocationID;
			gl_Position = m_lightViewProjection[ gl_InvocationID ] * m_World[matrixIndices[instanceID_GS[i]]] * gl_in[i].gl_Position;
			texCoord_FS = texCoord_GS[i];
			EmitVertex();
		}	
		EndPrimitive();
}
