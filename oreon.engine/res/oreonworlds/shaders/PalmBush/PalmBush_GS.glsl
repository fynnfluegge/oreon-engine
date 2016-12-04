#version 430

layout(triangles) in;

layout(triangle_strip, max_vertices = 3) out;

in vec2 texCoord1[];
in vec3 normal1[];
in vec3 position1[];

out vec2 texCoord2;
out vec3 position2;
out vec3 normal2;

uniform mat4 viewProjectionMatrix;
uniform vec4 clipplane;
uniform vec4 frustumPlanes[6];


void main()
{	
	
	vec4 position00 = gl_in[0].gl_Position;
    gl_Position = viewProjectionMatrix * position00;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	gl_ClipDistance[6] = dot(position00 ,clipplane);
	texCoord2 = texCoord1[0];
	position2 = position1[0];
	normal2 = normal1[0];
    EmitVertex();
	
	vec4 position01 = gl_in[1].gl_Position;
	gl_Position = viewProjectionMatrix * position01;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	gl_ClipDistance[6] = dot(position01 ,clipplane);
	texCoord2 = texCoord1[1];
	position2 = position1[1];
	normal2 = normal1[1];
    EmitVertex();

	vec4 position02 = gl_in[2].gl_Position;
	gl_Position = viewProjectionMatrix * position02;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	gl_ClipDistance[6] = dot(position02 ,clipplane);
	texCoord2 = texCoord1[2];
	position2 = position1[2];
	normal2 = normal1[2];
    EmitVertex();
	
    EndPrimitive();
}