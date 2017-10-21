#version 430

layout(triangles) in;
layout(line_strip, max_vertices = 4) out;

in vec2 texCoordG[];

struct Fractal
{
	sampler2D normalmap;
	int scaling;
};

struct Material
{
	sampler2D diffusemap;
	sampler2D normalmap;
	sampler2D heightmap;
	sampler2D splatmap;
	float displaceScale;
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

uniform Material sand;
uniform Material rock;
uniform Material cliff;
uniform vec4 clipplane;
uniform int largeDetailedRange;
uniform Fractal fractals1[4];
uniform float scaleXZ;

vec4 displacement[3];

void main() {
	
	for (int i = 0; i < 3; ++i){
		displacement[i] = vec4(0,0,0,0);
	}
	
	float dist = (distance(gl_in[0].gl_Position.xyz, eyePosition) + distance(gl_in[1].gl_Position.xyz, eyePosition) + distance(gl_in[2].gl_Position.xyz, eyePosition))/3;
	if (dist < largeDetailedRange){
	
		for(int k=0; k<gl_in.length(); k++){
		
			vec2 mapCoords = (gl_in[k].gl_Position.xz + scaleXZ/2)/scaleXZ; 
				
				displacement[k] = vec4(0,1,0,0);
				
				float height = gl_in[k].gl_Position.y;
				
				float sandBlend = texture(sand.splatmap, mapCoords).r;
				float rockBlend = texture(rock.splatmap, mapCoords).r;
				float cliffBlend = texture(cliff.splatmap, mapCoords).r;
				
				float scale = texture(sand.heightmap, texCoordG[k]/2).r * sandBlend * sand.displaceScale
							+ texture(rock.heightmap, texCoordG[k]/10).r * rockBlend * rock.displaceScale
							+ texture(cliff.heightmap, texCoordG[k]/10).r * cliffBlend * cliff.displaceScale;
							
				float attenuation = clamp(- distance(gl_in[k].gl_Position.xyz, eyePosition)/(largeDetailedRange-50) + 1,0.0,1.0);
				scale *= attenuation;

				displacement[k] *= scale;
		}	
	}
	
	for (int i = 0; i < gl_in.length(); ++i)
	{
		vec4 position = gl_in[i].gl_Position + displacement[i];
		gl_Position = m_ViewProjection * position;
		gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
		gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
		gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
		gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
		gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
		gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
		gl_ClipDistance[6] = dot(position ,clipplane);
		EmitVertex();
	}
	
	vec4 vertexPos = gl_in[0].gl_Position + displacement[0];
	gl_Position = m_ViewProjection * vertexPos;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	gl_ClipDistance[6] = dot(vertexPos ,clipplane);
    EmitVertex();
	
	EndPrimitive();
}