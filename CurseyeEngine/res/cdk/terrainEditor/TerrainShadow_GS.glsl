#version 430

layout(triangles, invocations = 4) in;

layout(triangle_strip, max_vertices = 3) out;

in vec2 texCoordG[];

struct Material
{
	sampler2D displacemap;
	float displaceScale;
};

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

layout (std140, row_major) uniform LightViewProjections{
	mat4 m_lightViewProjection[4];
};

uniform Fractal fractals1[10];
uniform int largeDetailedRange;
uniform float scaleXZ;
uniform sampler2D splatmap;
uniform Material rockgrass;
uniform Material rock;
uniform Material snow;

vec2 mapCoords[3];
vec4 displacement[3];

void main()
{	
	for (int i = 0; i < 3; ++i){
		displacement[i] = vec4(0,0,0,0);
	}
	float dist = (distance(gl_in[0].gl_Position.xyz, eyePosition) + distance(gl_in[1].gl_Position.xyz, eyePosition) + distance(gl_in[2].gl_Position.xyz, eyePosition))/3;
	if (dist < largeDetailedRange){
		
		mapCoords[0] = (gl_in[0].gl_Position.xz + scaleXZ/2)/scaleXZ;
		mapCoords[1] = (gl_in[1].gl_Position.xz + scaleXZ/2)/scaleXZ;
		mapCoords[2] = (gl_in[2].gl_Position.xz + scaleXZ/2)/scaleXZ;
		
		float sandBlending0 = texture(splatmap, mapCoords[0]).b;
		float rockBlending0 = texture(splatmap, mapCoords[0]).g;
		float snowBlending0 = texture(splatmap, mapCoords[0]).r;
		
		float sandBlending1 = texture(splatmap, mapCoords[1]).b;
		float rockBlending1 = texture(splatmap, mapCoords[1]).g;
		float snowBlending1 = texture(splatmap, mapCoords[1]).r;
		
		float sandBlending2 = texture(splatmap, mapCoords[2]).b;
		float rockBlending2 = texture(splatmap, mapCoords[2]).g;
		float snowBlending2 = texture(splatmap, mapCoords[2]).r;
		
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

	for (int i = 0; i < gl_in.length(); ++i)
	{
		gl_Layer = gl_InvocationID;
		gl_Position = m_lightViewProjection[ gl_InvocationID ] * (gl_in[i].gl_Position + displacement[i]);
		EmitVertex();
	}	
	EndPrimitive();
}
