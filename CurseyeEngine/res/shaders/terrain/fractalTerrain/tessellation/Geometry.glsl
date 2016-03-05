#version 430

layout(triangles) in;

layout(triangle_strip, max_vertices = 3) out;

in vec2 texCoordG[];

out vec3 position;
flat out vec3 tangent;

struct Fractal
{
	sampler2D normalmap;
	sampler2D heightmap;
};

uniform Fractal fractals[10];
uniform int largeDetailedRange;
uniform mat4 projectionViewMatrix;
uniform vec3 eyePosition;
uniform float scaleY;
uniform float scaleXZ;
uniform vec4 clipplane;
uniform vec4 frustumPlanes[6];

vec3 Tangent;
vec2 mapCoords0, mapCoords1, mapCoords2;
float displacement0, displacement1, displacement2;
vec4 displace0, displace1, displace2;


void calcTangent()
{	
	vec3 v0 = gl_in[0].gl_Position.xyz;
	vec3 v1 = gl_in[1].gl_Position.xyz;
	vec3 v2 = gl_in[2].gl_Position.xyz;
	
	mapCoords0 = (gl_in[0].gl_Position.xz + scaleXZ/2)/scaleXZ;
	mapCoords1 = (gl_in[1].gl_Position.xz + scaleXZ/2)/scaleXZ;
	mapCoords2 = (gl_in[2].gl_Position.xz + scaleXZ/2)/scaleXZ;

    vec3 e1 = v1 - v0;
    vec3 e2 = v2 - v0;

    float dU1 = mapCoords1.x - mapCoords0.x;
    float dV1 = mapCoords1.y - mapCoords0.y;
    float dU2 = mapCoords2.x - mapCoords0.x;
    float dV2 = mapCoords2.y - mapCoords0.y;

    float f = 1.0 / (dU1 * dV2 - dU2 * dV1);

    vec3 t;

    t.x = f * (dV2 * e1.x - dV1 * e2.x);
    t.y = f * (dV2 * e1.y - dV1 * e2.y);
    t.z = f * (dV2 * e1.z - dV1 * e2.z);
	
	Tangent = normalize(t);
}

void main()
{	
	float dist = (distance(gl_in[0].gl_Position.xyz, eyePosition) + distance(gl_in[1].gl_Position.xyz, eyePosition) + distance(gl_in[0].gl_Position.xyz, eyePosition))/3;
	if (dist < largeDetailedRange){
		calcTangent();
		
		mapCoords0 = (gl_in[0].gl_Position.xz + scaleXZ/2)/scaleXZ;
		mapCoords1 = (gl_in[1].gl_Position.xz + scaleXZ/2)/scaleXZ;
		mapCoords2 = (gl_in[2].gl_Position.xz + scaleXZ/2)/scaleXZ;
		
		float displace00 = texture(fractals[7].heightmap, mapCoords0*400).r;
		float displace01 = texture(fractals[8].heightmap, mapCoords0*410).r;
		float displace02 = texture(fractals[9].heightmap, mapCoords0*420).r;
		
		float displace10 = texture(fractals[7].heightmap, mapCoords1*400).r;
		float displace11 = texture(fractals[8].heightmap, mapCoords1*410).r;
		float displace12 = texture(fractals[9].heightmap, mapCoords1*420).r;
		
		float displace20 = texture(fractals[7].heightmap, mapCoords2*400).r;
		float displace21 = texture(fractals[8].heightmap, mapCoords2*410).r;
		float displace22 = texture(fractals[9].heightmap, mapCoords2*420).r;
	
		displacement0 = ((displace00 + displace01 + displace02) *4)*
							(- distance(gl_in[0].gl_Position.xyz, eyePosition)/largeDetailedRange + 1);
		displacement1 = ((displace10 + displace11 + displace12) *4)*
							(- distance(gl_in[1].gl_Position.xyz, eyePosition)/largeDetailedRange + 1);
		displacement2 = ((displace20 + displace21 + displace22) *4)*
							(- distance(gl_in[2].gl_Position.xyz, eyePosition)/largeDetailedRange + 1);
		
		displace0 = vec4(normalize((2*(texture(fractals[1].normalmap,mapCoords0* 2).rbg)-1)
								 + (2*(texture(fractals[2].normalmap,mapCoords0* 4).rbg)-1)),0);
		displace0.y /= 2;
		displace0 *= displacement0;
		
		displace1 = vec4(normalize((2*(texture(fractals[1].normalmap,mapCoords1* 2).rbg)-1)
								 + (2*(texture(fractals[2].normalmap,mapCoords1* 4).rbg)-1)),0);
		displace1.y /= 2;
		displace1 *= displacement1;
		
		displace2 = vec4(normalize((2*(texture(fractals[1].normalmap,mapCoords2* 2).rbg)-1)
								 + (2*(texture(fractals[2].normalmap,mapCoords2* 4).rbg)-1)),0);
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
	position = (position0).xyz;
	tangent = Tangent;
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
	position = (position1).xyz;
	tangent = Tangent;
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
	position = (position2).xyz;
	tangent = Tangent;
    EmitVertex();
	
    EndPrimitive();
}