#version 430

layout(triangles) in;

layout(triangle_strip, max_vertices = 3) out;

out vec3 position;
out vec4 viewSpacePos;
out vec3 tangent;

struct Fractal
{
	sampler2D heightmap;
	int scaling;
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

uniform Fractal fractals1[3];
uniform int largeDetailedRange;
uniform float scaleY;
uniform float scaleXZ;
uniform vec4 clipplane;

vec3 Tangent;
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
	float dist = (distance(gl_in[0].gl_Position.xyz, eyePosition) + distance(gl_in[1].gl_Position.xyz, eyePosition) + distance(gl_in[2].gl_Position.xyz, eyePosition))/3;
	if (dist < largeDetailedRange){
		calcTangent();
	}
	
	for (int i=0; i<gl_in.length(); i++)
	{
		vec4 vertexPos = gl_in[i].gl_Position;
		gl_Position = m_ViewProjection * vertexPos;
		gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
		gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
		gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
		gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
		gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
		gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
		gl_ClipDistance[6] = dot(vertexPos ,clipplane);
		viewSpacePos = m_View * vertexPos;
		position = (vertexPos).xyz;
		tangent = Tangent;
		EmitVertex();
	}
	
	EndPrimitive();
}