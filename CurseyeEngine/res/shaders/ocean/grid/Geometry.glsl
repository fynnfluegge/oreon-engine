#version 430

layout(triangles) in;
layout(line_strip, max_vertices = 4) out;

in vec2 texCoordG[];

uniform mat4 projectionViewMatrix;
uniform vec4 frustumPlanes[6];
uniform float displacementScale;
uniform sampler2D Dy;
uniform sampler2D Dx;
uniform sampler2D Dz;
uniform float choppiness;

void main()
{	
	float dx,dy,dz;

	dy = texture(Dy, texCoordG[0]).r * displacementScale;
	dx = texture(Dx, texCoordG[0]).r * choppiness;
	dz = texture(Dz, texCoordG[0]).r * choppiness;
	vec4 position0 = gl_in[0].gl_Position;
	position0.y += dy;
	position0.x += dx;
	position0.z += dz;
    gl_Position = projectionViewMatrix * position0;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
    EmitVertex();
	
	dy = texture(Dy, texCoordG[1]).r * displacementScale;
	dx = texture(Dx, texCoordG[1]).r * choppiness;
	dz = texture(Dz, texCoordG[1]).r * choppiness;
	vec4 position1 = gl_in[1].gl_Position;
	position1.y += dy;
	position1.x += dx;
	position1.z += dz;
	gl_Position = projectionViewMatrix * position1;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
    EmitVertex();

	dy = texture(Dy, texCoordG[2]).r * displacementScale;
	dx = texture(Dx, texCoordG[2]).r * choppiness;
	dz = texture(Dz, texCoordG[2]).r * choppiness;
	vec4 position2 = gl_in[2].gl_Position;
	position2.y += dy;
	position2.x += dx;
	position2.z += dz;
	gl_Position = projectionViewMatrix * position2;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
    EmitVertex();
	
    gl_Position = projectionViewMatrix * position0;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
    EmitVertex();
	
    EndPrimitive();
}