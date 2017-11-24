#version 430

layout(triangles) in;
layout(line_strip, max_vertices = 4) out;

in vec2 texCoordG[];

struct Material
{
	sampler2D heightmap;
	sampler2D alphamap;
	float heightScaling;
	float horizontalScaling;
};

out vec2 texCoordF;
out vec4 viewSpacePos;
out vec3 position;

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

uniform Material materials[5];
uniform int largeDetailRange;
uniform vec4 clipplane;
uniform float scaleXZ;

vec3 displacement[3];

void main() {	

	for (int i = 0; i < 3; ++i){
		displacement[i] = vec3(0,0,0);
	}

	float dist = (distance(gl_in[0].gl_Position.xyz, eyePosition) + distance(gl_in[1].gl_Position.xyz, eyePosition) + distance(gl_in[2].gl_Position.xyz, eyePosition))/3;
	
	if (dist < (largeDetailRange)){
		
		for(int k=0; k<gl_in.length(); k++){
			
			vec2 mapCoords = (gl_in[k].gl_Position.xz + scaleXZ/2)/scaleXZ; 
			
			displacement[k] = vec3(0,1,0);
			
			float height = gl_in[k].gl_Position.y;
			
			float scale = 0;
			for (int i=0; i<5; i++){
				scale += texture(materials[i].heightmap, texCoordG[k]/materials[i].horizontalScaling).r * texture(materials[i].alphamap, mapCoords).r * materials[i].heightScaling;
			}
						
			float attenuation = clamp(- distance(gl_in[k].gl_Position.xyz, eyePosition)/(largeDetailRange-50) + 1,0.0,1.0);
			scale *= attenuation;

			displacement[k] *= scale;
		}	
	}
	
	for (int i = 0; i < gl_in.length(); ++i)
	{
		vec4 position = gl_in[i].gl_Position + vec4(displacement[i],0);
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
	
	vec4 vertexPos = gl_in[0].gl_Position + vec4(displacement[0],0);
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