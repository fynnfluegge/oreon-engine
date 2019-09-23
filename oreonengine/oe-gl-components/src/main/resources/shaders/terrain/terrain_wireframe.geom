#version 450
#extension GL_ARB_separate_shader_objects : enable

#lib.glsl

layout(triangles) in;
layout(line_strip, max_vertices = 4) out;

layout (location = 0) in vec2 inUV[];

struct Material
{
	sampler2D heightmap;
	float heightScaling;
	float uvScaling;
};

out vec2 texCoordF;
out vec4 viewSpacePos;
out vec3 position;

layout (std430, binding = 1) buffer ssbo0 {
	vec3 fogColor;
	float sightRangeFactor;
	int diamond_square_enable;
	int tessFactor;
	float tessSlope;
	float tessShift;
	float xzScale;
	int isBezier;
	float uvScale;
	int largeDetailRange;
};

uniform sampler2D splatmap;
uniform Material materials[5];
uniform vec4 clipplane;

void main() {	

	vec3 displacement[3] = { vec3(0), vec3(0), vec3(0) };

	float dist = (distance(gl_in[0].gl_Position.xyz, camera.eyePosition)
		+ distance(gl_in[1].gl_Position.xyz, camera.eyePosition) + distance(gl_in[2].gl_Position.xyz, camera.eyePosition))/3;
	
	if (dist < (largeDetailRange)){
		
		for(int k=0; k<gl_in.length(); k++){
			
			vec2 mapCoords = (gl_in[k].gl_Position.xz + xzScale/2)/xzScale; 
			
			vec4 v_splatmap = texture(splatmap, mapCoords).rgba;
			float[4] blendValues = float[](v_splatmap.r,v_splatmap.g,v_splatmap.b,v_splatmap.a);
			
			displacement[k] = vec3(0,1,0);
			
			float height = gl_in[k].gl_Position.y;
			
			float scale = 0;
			for (int i=0; i<4; i++){
				scale += texture(materials[i].heightmap, inUV[k]/materials[i].uvScaling).r * materials[i].heightScaling * blendValues[i];
			}
						
			float attenuation = clamp(- distance(gl_in[k].gl_Position.xyz, camera.eyePosition)/(largeDetailRange-50) + 1,0.0,1.0);
			scale *= attenuation;

			displacement[k] *= scale;
		}	
	}
	
	for (int i = 0; i < gl_in.length(); ++i)
	{
		vec4 position = gl_in[i].gl_Position + vec4(displacement[i],0);
		gl_Position = camera.m_ViewProjection * position;
		gl_ClipDistance[0] = dot(gl_Position, camera.frustumPlanes[0]);
		gl_ClipDistance[1] = dot(gl_Position, camera.frustumPlanes[1]);
		gl_ClipDistance[2] = dot(gl_Position, camera.frustumPlanes[2]);
		gl_ClipDistance[3] = dot(gl_Position, camera.frustumPlanes[3]);
		gl_ClipDistance[4] = dot(gl_Position, camera.frustumPlanes[4]);
		gl_ClipDistance[5] = dot(gl_Position, camera.frustumPlanes[5]);
		gl_ClipDistance[6] = dot(position ,clipplane);
		EmitVertex();
	}
	
	vec4 vertexPos = gl_in[0].gl_Position + vec4(displacement[0],0);
	gl_Position = camera.m_ViewProjection * vertexPos;
	gl_ClipDistance[0] = dot(gl_Position, camera.frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position, camera.frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position, camera.frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position, camera.frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position, camera.frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position, camera.frustumPlanes[5]);
	gl_ClipDistance[6] = dot(vertexPos ,clipplane);
    EmitVertex();
	
	EndPrimitive();
}