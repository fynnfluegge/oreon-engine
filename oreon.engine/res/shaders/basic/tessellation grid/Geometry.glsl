#version 430

layout(triangles) in;
layout(line_strip, max_vertices = 4) out;

in vec2 texCoord3[];

struct Material
{
	sampler2D displacemap;
	float displaceScale;
};

uniform mat4 viewProjectionMatrix;
uniform int displacement;
uniform vec4 clipplane;
uniform vec4 frustumPlanes[6];
uniform Material material;

float displacement0, displacement1, displacement2;
vec4 displace0, displace1, displace2;
vec3 normal0;

void calcNormal()
{
	vec3 v0 = gl_in[0].gl_Position.xyz;
	vec3 v1 = gl_in[1].gl_Position.xyz;
	vec3 v2 = gl_in[2].gl_Position.xyz;
	
	normal0 = normalize(cross(v0-v1,v0-v2));
}

void main() {

	calcNormal();	
	
	if (displacement == 1){
		displacement0 = texture(material.displacemap, texCoord3[0]).r * material.displaceScale;
		displacement1 = texture(material.displacemap, texCoord3[1]).r * material.displaceScale;
		displacement2 = texture(material.displacemap, texCoord3[2]).r * material.displaceScale;
	
		displace0 = vec4(normal0 * displacement0, 0);
		displace1 = vec4(normal0 * displacement1, 0);
		displace2 = vec4(normal0 * displacement2, 0);
	}
	
	vec4 position0 = gl_in[0].gl_Position + displace0;
	gl_Position = viewProjectionMatrix * position0;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	gl_ClipDistance[6] = dot(position0 ,clipplane);
    EmitVertex();
	
	vec4 position1 = gl_in[1].gl_Position + displace1;
	gl_Position = viewProjectionMatrix * position1;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	gl_ClipDistance[6] = dot(position1 ,clipplane);
    EmitVertex();

	vec4 position2 = gl_in[2].gl_Position + displace2;
	gl_Position = viewProjectionMatrix * position2;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	gl_ClipDistance[6] = dot(position2 ,clipplane);
    EmitVertex();
	
	gl_Position = viewProjectionMatrix * position0;
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