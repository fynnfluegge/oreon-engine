#version 430

layout(triangles) in;
layout(line_strip, max_vertices = 4) out;

in vec2 texCoordG[];

struct Fractal
{
	sampler2D heightmap;
	sampler2D normalmap;
	int scaling;
	float strength;
};

uniform Fractal fractals[10];
uniform vec3 eyePosition;
uniform int largeDetailedRange;
uniform mat4 projectionViewMatrix;
uniform float scaleY;
uniform float scaleXZ;
uniform vec4 clipplane;
uniform vec4 frustumPlanes[6];

vec2 mapCoords0, mapCoords1, mapCoords2;
float displacement0, displacement1, displacement2;
vec4 displace0, displace1, displace2;

void main() {

	float dist = (distance(gl_in[0].gl_Position.xyz, eyePosition) + distance(gl_in[1].gl_Position.xyz, eyePosition) + distance(gl_in[0].gl_Position.xyz, eyePosition))/3;
	if (dist < largeDetailedRange){
	
		mapCoords0 = (gl_in[0].gl_Position.xz + scaleXZ/2)/scaleXZ;
		mapCoords1 = (gl_in[1].gl_Position.xz + scaleXZ/2)/scaleXZ;
		mapCoords2 = (gl_in[2].gl_Position.xz + scaleXZ/2)/scaleXZ;
		
		float displace00 = texture(fractals[7].heightmap, mapCoords0*fractals[7].scaling).r;
		float displace01 = texture(fractals[8].heightmap, mapCoords0*fractals[8].scaling).r;
		float displace02 = texture(fractals[9].heightmap, mapCoords0*fractals[9].scaling).r;
		
		float displace10 = texture(fractals[7].heightmap, mapCoords1*fractals[7].scaling).r;
		float displace11 = texture(fractals[8].heightmap, mapCoords1*fractals[8].scaling).r;
		float displace12 = texture(fractals[9].heightmap, mapCoords1*fractals[9].scaling).r;
		
		float displace20 = texture(fractals[7].heightmap, mapCoords2*fractals[7].scaling).r;
		float displace21 = texture(fractals[8].heightmap, mapCoords2*fractals[8].scaling).r;
		float displace22 = texture(fractals[9].heightmap, mapCoords2*fractals[9].scaling).r;
	
		displacement0 = ((displace00 + displace01 + displace02) *4)*
							(- distance(gl_in[0].gl_Position.xyz, eyePosition)/largeDetailedRange + 1);
		displacement1 = ((displace10 + displace11 + displace12) *4)*
							(- distance(gl_in[1].gl_Position.xyz, eyePosition)/largeDetailedRange + 1);
		displacement2 = ((displace20 + displace21 + displace22) *4)*
							(- distance(gl_in[2].gl_Position.xyz, eyePosition)/largeDetailedRange + 1);
		
		displace0 = vec4(normalize((2*(texture(fractals[1].normalmap,mapCoords0*fractals[1].scaling).rbg)-1)
								 + (2*(texture(fractals[2].normalmap,mapCoords0*fractals[2].scaling).rbg)-1)),0);
		displace0.y /= 2;
		displace0 *= displacement0;
		
		displace1 = vec4(normalize((2*(texture(fractals[1].normalmap,mapCoords1*fractals[1].scaling).rbg)-1)
								 + (2*(texture(fractals[2].normalmap,mapCoords1*fractals[2].scaling).rbg)-1)),0);
		displace1.y /= 2;
		displace1 *= displacement1;
		
		displace2 = vec4(normalize((2*(texture(fractals[1].normalmap,mapCoords2*fractals[1].scaling).rbg)-1)
								 + (2*(texture(fractals[2].normalmap,mapCoords2*fractals[2].scaling).rbg)-1)),0);
		displace2.y /= 2;
		displace2 *= displacement2;
	}
	
	vec4 position0 = gl_in[0].gl_Position + displace0;
	gl_Position = projectionViewMatrix * position0;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	gl_ClipDistance[6] = dot(position0 ,clipplane);
    EmitVertex();
	
	vec4 position1 = gl_in[1].gl_Position + displace1;
	gl_Position = projectionViewMatrix * position1;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	gl_ClipDistance[6] = dot(position1 ,clipplane);
    EmitVertex();

	vec4 position2 = gl_in[2].gl_Position + displace2;
	gl_Position = projectionViewMatrix * position2;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	gl_ClipDistance[6] = dot(position2 ,clipplane);
    EmitVertex();
	
	gl_Position = projectionViewMatrix * position0;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	gl_ClipDistance[6] = dot(position0 ,clipplane);
    EmitVertex();
	
    EndPrimitive();
}