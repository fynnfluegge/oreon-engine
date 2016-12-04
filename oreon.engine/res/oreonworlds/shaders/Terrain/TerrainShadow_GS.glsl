#version 430

layout(triangles, invocations = 4) in;

layout(triangle_strip, max_vertices = 3) out;

in vec2 texCoordG[];


layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
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
		gl_Position = m_lightViewProjection[ gl_InvocationID ] * (gl_in[i].gl_Position);
		EmitVertex();
	}	
	EndPrimitive();
}
