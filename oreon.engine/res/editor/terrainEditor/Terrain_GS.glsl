#version 430

layout(triangles) in;

layout(triangle_strip, max_vertices = 3) out;

in vec2 texCoordG[];

out vec2 texCoordF;
out vec3 position;
out vec3 tangent;

struct Material
{
	sampler2D displacemap;
	float displaceScale;
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 viewProjectionMatrix;
	vec4 frustumPlanes[6];
};

uniform int largeDetailedRange;
uniform vec4 clipplane;

vec3 Tangent;

void calcTangent()
{	
	vec3 v0 = gl_in[0].gl_Position.xyz;
	vec3 v1 = gl_in[1].gl_Position.xyz;
	vec3 v2 = gl_in[2].gl_Position.xyz;

	// edges of the face/triangle
    vec3 e1 = v1 - v0;
    vec3 e2 = v2 - v0;
	
	vec2 uv0 = texCoordG[0];
	vec2 uv1 = texCoordG[1];
	vec2 uv2 = texCoordG[2];

    vec2 deltaUV1 = uv1 - uv0;
	vec2 deltaUV2 = uv2 - uv0;
	
	float r = 1.0 / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);
	
	Tangent = normalize((e1 * deltaUV2.y - e2 * deltaUV1.y)*r);
}



void main() {

	float dist = (distance(gl_in[0].gl_Position.xyz, eyePosition) + distance(gl_in[1].gl_Position.xyz, eyePosition) + distance(gl_in[2].gl_Position.xyz, eyePosition))/3;
	if (dist < largeDetailedRange){
		
		calcTangent();
	}
	
	for (int i = 0; i < gl_in.length(); ++i)
	{
		vec4 vertexPos = gl_in[i].gl_Position;
		gl_Position = viewProjectionMatrix * vertexPos;
		gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
		gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
		gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
		gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
		gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
		gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
		gl_ClipDistance[6] = dot(vertexPos ,clipplane);
		texCoordF = texCoordG[i];
		position = vertexPos.xyz;
		tangent = Tangent;
		EmitVertex();
	}
	EndPrimitive();
}