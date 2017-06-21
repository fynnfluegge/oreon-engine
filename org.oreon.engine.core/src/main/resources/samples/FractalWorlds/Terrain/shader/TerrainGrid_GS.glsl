#version 430

layout(triangles) in;
layout(line_strip, max_vertices = 4) out;

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

uniform vec4 clipplane;

void main() {
	
	for (int i=0; i<gl_in.length(); i++){
		vec4 vertexPos = gl_in[i].gl_Position;
		gl_Position = m_ViewProjection * vertexPos;
		gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
		gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
		gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
		gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
		gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
		gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
		gl_ClipDistance[6] = dot(vertexPos ,clipplane);
		EmitVertex();
	}
	vec4 vertexPos = gl_in[0].gl_Position;
	gl_Position = m_ViewProjection * vertexPos;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	gl_ClipDistance[6] = dot(vertexPos ,clipplane);
    EmitVertex();
	
    EndPrimitive();
}