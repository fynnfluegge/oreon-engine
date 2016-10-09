#version 430

layout(triangles) in;

layout(triangle_strip, max_vertices = 3) out;

out vec3 position;
out vec3 tangent;

struct Fractal
{
	sampler2D heightmap;
	sampler2D normalmap;
	int scaling;
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 viewProjectionMatrix;
	vec4 frustumPlanes[6];
};

uniform Fractal fractals1[10];
uniform int largeDetailedRange;
uniform float scaleY;
uniform float scaleXZ;
uniform vec4 clipplane;

vec3 Tangent;
vec4 displacement[3];
vec2 mapCoords[3];

void calcTangent()
{	
	vec3 v0 = gl_in[0].gl_Position.xyz;
	vec3 v1 = gl_in[1].gl_Position.xyz;
	vec3 v2 = gl_in[2].gl_Position.xyz;
	
	mapCoords[0] = (gl_in[0].gl_Position.xz + scaleXZ/2)/scaleXZ;
	mapCoords[1] = (gl_in[1].gl_Position.xz + scaleXZ/2)/scaleXZ;
	mapCoords[2] = (gl_in[2].gl_Position.xz + scaleXZ/2)/scaleXZ;

    vec3 e1 = v1 - v0;
    vec3 e2 = v2 - v0;

    float dU1 = mapCoords[1].x - mapCoords[0].x;
    float dV1 = mapCoords[1].y - mapCoords[0].y;
    float dU2 = mapCoords[2].x - mapCoords[0].x;
    float dV2 = mapCoords[2].y - mapCoords[0].y;

    float f = 1.0 / (dU1 * dV2 - dU2 * dV1);

    vec3 t;

    t.x = f * (dV2 * e1.x - dV1 * e2.x);
    t.y = f * (dV2 * e1.y - dV1 * e2.y);
    t.z = f * (dV2 * e1.z - dV1 * e2.z);
	
	Tangent = normalize(t);
}

void main()
{	
	for (int i = 0; i < 3; ++i){
		displacement[i] = vec4(0,0,0,0);
	}
	float dist = (distance(gl_in[0].gl_Position.xyz, eyePosition) + distance(gl_in[1].gl_Position.xyz, eyePosition) + distance(gl_in[2].gl_Position.xyz, eyePosition))/3;
	if (dist < largeDetailedRange){
		calcTangent();
		
		mapCoords[0] = (gl_in[0].gl_Position.xz + scaleXZ/2)/scaleXZ;
		mapCoords[1] = (gl_in[1].gl_Position.xz + scaleXZ/2)/scaleXZ;
		mapCoords[2] = (gl_in[2].gl_Position.xz + scaleXZ/2)/scaleXZ;
		
		for(int k=0; k<3; k++){
			float scale = texture(fractals1[7].heightmap, mapCoords[k]*fractals1[7].scaling).r
						+ texture(fractals1[8].heightmap, mapCoords[k]*fractals1[8].scaling).r
						+ texture(fractals1[9].heightmap, mapCoords[k]*fractals1[9].scaling).r;
			scale *= 4;
			scale *= (- distance(gl_in[k].gl_Position.xyz, eyePosition)/largeDetailedRange + 1);
			displacement[k] = vec4(normalize((2*(texture(fractals1[1].normalmap,mapCoords[k]*fractals1[1].scaling).rbg)-1)
								 + (2*(texture(fractals1[2].normalmap,mapCoords[k]*fractals1[2].scaling).rbg)-1)),0);
			displacement[k].y /= 2;
			displacement[k] *= scale;
		}
	}
	
	for (int i=0; i<gl_in.length(); i++){
		vec4 vertexPos = gl_in[i].gl_Position + displacement[i];
		gl_Position = viewProjectionMatrix * vertexPos;
		gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
		gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
		gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
		gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
		gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
		gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
		gl_ClipDistance[6] = dot(vertexPos ,clipplane);
		position = (vertexPos).xyz;
		tangent = Tangent;
		EmitVertex();
	}
	
	EndPrimitive();
}