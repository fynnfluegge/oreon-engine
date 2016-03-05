#version 430

layout(triangles) in;
layout(line_strip, max_vertices = 4) out;

in vec2 texCoordG[];

struct Material
{
	sampler2D displacemap;
	float displaceScale;
};

struct Fractal
{
	sampler2D normalmap;
};

uniform Fractal fractals[10];
uniform vec3 eyePosition;
uniform int largeDetailedRange;
uniform mat4 projectionViewMatrix;
uniform float scaleY;
uniform float scaleXZ;
uniform sampler2D normalmap;
uniform sampler2D splatmap;
uniform Material rockgrass;
uniform Material rock;
uniform Material snow;
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
		
		float rockgrassBlending0 = texture(splatmap, mapCoords0).b;
		float rockBlending0 = texture(splatmap, mapCoords0).g;
		float snowBlending0 = texture(splatmap, mapCoords0).r;
		
		float rockgrassBlending1 = texture(splatmap, mapCoords1).b;
		float rockBlending1 = texture(splatmap, mapCoords1).g;
		float snowBlending1 = texture(splatmap, mapCoords1).r;
		
		float rockgrassBlending2 = texture(splatmap, mapCoords2).b;
		float rockBlending2 = texture(splatmap, mapCoords2).g;
		float snowBlending2 = texture(splatmap, mapCoords2).r;
		
		float displaceScale = scaleY*0.002;
		
		float displaceRockgrass0 = texture(rockgrass.displacemap, texCoordG[0]).r * (rockgrass.displaceScale * displaceScale);
		float displaceRock0 = texture(rock.displacemap, texCoordG[0]).r * (rock.displaceScale * displaceScale);
		float displaceSnow0 = texture(snow.displacemap, texCoordG[0]).r * (snow.displaceScale * displaceScale);
		
		float displaceRockgrass1 = texture(rockgrass.displacemap, texCoordG[1]).r * (rockgrass.displaceScale * displaceScale);
		float displaceRock1 = texture(rock.displacemap, texCoordG[1]).r * (rock.displaceScale * displaceScale);
		float displaceSnow1 = texture(snow.displacemap, texCoordG[1]).r * (snow.displaceScale * displaceScale);
		
		float displaceRockgrass2 = texture(rockgrass.displacemap, texCoordG[2]).r * (rockgrass.displaceScale * displaceScale);
		float displaceRock2 = texture(rock.displacemap, texCoordG[2]).r * (rock.displaceScale * displaceScale);
		float displaceSnow2 = texture(snow.displacemap, texCoordG[2]).r * (snow.displaceScale * displaceScale);
	
		displacement0 = rockgrassBlending0 * displaceRockgrass0 + rockBlending0 * displaceRock0 + snowBlending0 * displaceSnow0;
		displacement1 = rockgrassBlending1 * displaceRockgrass1 + rockBlending1 * displaceRock1 + snowBlending1 * displaceSnow1;
		displacement2 = rockgrassBlending2 * displaceRockgrass2 + rockBlending2 * displaceRock2 + snowBlending2 * displaceSnow2;
		
		displace0 = vec4(normalize((2*(texture(normalmap,mapCoords0).rbg)-1)
							+	(2*(texture(fractals[0].normalmap,mapCoords0* 2).rbg)-1)
							+	(2*(texture(fractals[1].normalmap,mapCoords0* 4).rbg)-1)
							+	(2*(texture(fractals[2].normalmap,mapCoords0* 8).rbg)-1)) * displacement0,0);
		displace1 = vec4(normalize((2*(texture(normalmap,mapCoords1).rbg)-1)
							+	(2*(texture(fractals[0].normalmap,mapCoords1* 2).rbg)-1)
							+	(2*(texture(fractals[1].normalmap,mapCoords1* 4).rbg)-1)
							+	(2*(texture(fractals[2].normalmap,mapCoords1* 8).rbg)-1)) * displacement1,0);
		displace2 = vec4(normalize((2*(texture(normalmap,mapCoords2).rbg)-1)
							+	(2*(texture(fractals[0].normalmap,mapCoords2* 2).rbg)-1)
							+	(2*(texture(fractals[1].normalmap,mapCoords2* 4).rbg)-1)
							+	(2*(texture(fractals[2].normalmap,mapCoords2* 8).rbg)-1)) * displacement2,0);
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