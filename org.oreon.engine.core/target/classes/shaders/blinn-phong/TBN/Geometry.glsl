#version 430

layout(triangles) in;

layout(triangle_strip, max_vertices = 3) out;

in vec2 texCoord1[];
in vec3 normal1[];
in vec3 tangent1[];
in vec3 bitangent1[];
in vec3 position1[];

out vec2 texCoord2;
out vec3 position2;
out vec3 normal2;
out vec3 tangent2;
out vec3 bitangent2;

struct Material
{
	sampler2D displacemap;
	float displaceScale;
};

uniform Material material1;
uniform mat4 viewProjectionMatrix;
uniform vec4 clipplane;
uniform vec4 frustumPlanes[6];
uniform int displacement;

float displacement0, displacement1, displacement2;
vec4 displace0, displace1, displace2;

void calcTangent()
{	
	vec3 v0 = gl_in[0].gl_Position.xyz;
	vec3 v1 = gl_in[1].gl_Position.xyz;
	vec3 v2 = gl_in[2].gl_Position.xyz;
	
	vec2 uv0 = texCoord1[0];
	vec2 uv1 = texCoord1[1];
	vec2 uv2 = texCoord1[2];
	
	// edges of the triangle
	vec3 e1 = v1 - v0;
    vec3 e2 = v2 - v0;

	vec2 deltaUV1 = uv1 - uv0;
	vec2 deltaUV2 = uv2 - uv0;
	
	float r = 1.0 / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);
	vec3 tangent = normalize((e1 * deltaUV2.y - e2 * deltaUV1.y)*r);
	vec3 bitangent = normalize((e2 * deltaUV1.x - e1 * deltaUV2.x)*r);
}

void main()
{	
	displace0 = vec4(0,0,0,0);
	displace1 = vec4(0,0,0,0);
	displace2 = vec4(0,0,0,0);
	displacement0 = 0;
	displacement1 = 0;
	displacement2 = 0;
	
	if (displacement == 1){
		displacement0 = texture(material1.displacemap, texCoord1[0]).r * material1.displaceScale;
		displacement1 = texture(material1.displacemap, texCoord1[1]).r * material1.displaceScale;
		displacement2 = texture(material1.displacemap, texCoord1[2]).r * material1.displaceScale;
	
		displace0 = vec4(normal1[0] * displacement0, 0);
		displace1 = vec4(normal1[1] * displacement1, 0);
		displace2 = vec4(normal1[2] * displacement2, 0);
	}
	
	vec4 position00 = gl_in[0].gl_Position + displace0;
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
	tangent2 = tangent1[0];
	bitangent2 = bitangent1[0];
    EmitVertex();
	
	vec4 position01 = gl_in[1].gl_Position + displace1;
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
	tangent2 = tangent1[1];
	bitangent2 = bitangent1[1];
    EmitVertex();

	vec4 position02 = gl_in[2].gl_Position + displace2;
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
	tangent2 = tangent1[2];
	bitangent2 = bitangent1[2];
    EmitVertex();
	
    EndPrimitive();
}