#version 430

layout(triangles) in;

layout(triangle_strip, max_vertices = 3) out;

in vec2 texCoordG[];
flat in int highDetailG[];
in int crackAvoidanceG[];

out vec2 texCoordF;
out vec3 positionF;
flat out int highDetailF;
flat out vec3 tangent;

struct Material
{
	sampler2D displacemap;
	float displaceScale;
};

uniform mat4 projectionViewMatrix;
uniform float scaleY;
uniform float scaleXZ;
uniform sampler2D normalmap;
uniform sampler2D splatmap;
uniform Material sand;
uniform Material rock;
uniform Material snow;
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

    vec3 e1 = v1 - v0;
    vec3 e2 = v2 - v0;

    float dU1 = texCoordG[1].x - texCoordG[0].x;
    float dV1 = texCoordG[1].y - texCoordG[0].y;
    float dU2 = texCoordG[2].x - texCoordG[0].x;
    float dV2 = texCoordG[2].y - texCoordG[0].y;

    float f = 1.0 / (dU1 * dV2 - dU2 * dV1);

    vec3 t;

    t.x = f * (dV2 * e1.x - dV1 * e2.x);
    t.y = f * (dV2 * e1.y - dV1 * e2.y);
    t.z = f * (dV2 * e1.z - dV1 * e2.z);
	
	Tangent = normalize(t);
}


void main()
{	
	if (crackAvoidanceG[0] == 1 && highDetailG[0] == 1) tangent = vec3(0,0,-1); //numeric error
	else if (highDetailG[0] == 1) calcTangent();
	if (crackAvoidanceG[0] == 4 && highDetailG[0] == 1)
	{
		mapCoords0 = (gl_in[0].gl_Position.xz + scaleXZ/2)/scaleXZ;
		mapCoords1 = (gl_in[1].gl_Position.xz + scaleXZ/2)/scaleXZ;
		mapCoords2 = (gl_in[2].gl_Position.xz + scaleXZ/2)/scaleXZ;
		
		float sandBlending0 = texture(splatmap, mapCoords0).b;
		float rockBlending0 = texture(splatmap, mapCoords0).g;
		float snowBlending0 = texture(splatmap, mapCoords0).r;
		
		float sandBlending1 = texture(splatmap, mapCoords1).b;
		float rockBlending1 = texture(splatmap, mapCoords1).g;
		float snowBlending1 = texture(splatmap, mapCoords1).r;
		
		float sandBlending2 = texture(splatmap, mapCoords2).b;
		float rockBlending2 = texture(splatmap, mapCoords2).g;
		float snowBlending2 = texture(splatmap, mapCoords2).r;
		
		float displaceScale = scaleY*0.002;
		
		float displaceSand0 = texture(sand.displacemap, texCoordG[0]).r * (sand.displaceScale * displaceScale);
		float displaceRock0 = texture(rock.displacemap, texCoordG[0]).r * (rock.displaceScale * displaceScale);
		float displaceSnow0 = texture(snow.displacemap, texCoordG[0]).r * (snow.displaceScale * displaceScale);
		
		float displaceSand1 = texture(sand.displacemap, texCoordG[1]).r * (sand.displaceScale * displaceScale);
		float displaceRock1 = texture(rock.displacemap, texCoordG[1]).r * (rock.displaceScale * displaceScale);
		float displaceSnow1 = texture(snow.displacemap, texCoordG[1]).r * (snow.displaceScale * displaceScale);
		
		float displaceSand2 = texture(sand.displacemap, texCoordG[2]).r * (sand.displaceScale * displaceScale);
		float displaceRock2 = texture(rock.displacemap, texCoordG[2]).r * (rock.displaceScale * displaceScale);
		float displaceSnow2 = texture(snow.displacemap, texCoordG[2]).r * (snow.displaceScale * displaceScale);
	
		displacement0 = sandBlending0 * displaceSand0 + rockBlending0 * displaceRock0 + snowBlending0 * displaceSnow0;
		displacement1 = sandBlending1 * displaceSand1 + rockBlending1 * displaceRock1 + snowBlending1 * displaceSnow1;
		displacement2 = sandBlending2 * displaceSand2 + rockBlending2 * displaceRock2 + snowBlending2 * displaceSnow2;
		
		displace0 = vec4(normalize((2*(texture(normalmap, vec2(mapCoords0.x, mapCoords0.y)).rbg)-1)) * displacement0, 0);
		displace1 = vec4(normalize((2*(texture(normalmap, vec2(mapCoords1.x, mapCoords1.y)).rbg)-1)) * displacement1, 0);
		displace2 = vec4(normalize((2*(texture(normalmap, vec2(mapCoords2.x, mapCoords2.y)).rbg)-1)) * displacement2, 0);
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
	texCoordF = texCoordG[0];
	positionF = (position0).xyz;
	highDetailF = highDetailG[0];
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
	texCoordF = texCoordG[1];
	positionF = (position1).xyz;
	highDetailF = highDetailG[1];
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
	texCoordF = texCoordG[2];
	positionF = (position2).xyz;
	highDetailF = highDetailG[2];
	tangent = Tangent;
    EmitVertex();
	
    EndPrimitive();
	
	
}