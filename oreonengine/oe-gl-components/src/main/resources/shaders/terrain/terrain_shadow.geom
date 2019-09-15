#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(triangles, invocations = 1) in;
layout(triangle_strip, max_vertices = 3) out;

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

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

layout (std140, row_major) uniform LightViewProjections{
	mat4 m_lightViewProjection[5];
};

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

	float dist = (distance(gl_in[0].gl_Position.xyz, eyePosition) + distance(gl_in[1].gl_Position.xyz, eyePosition) + distance(gl_in[2].gl_Position.xyz, eyePosition))/3;
	
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
						
			float attenuation = clamp(- distance(gl_in[k].gl_Position.xyz, eyePosition)/(largeDetailRange-50) + 1,0.0,1.0);
			scale *= attenuation;

			displacement[k] *= scale;
		}	
	}
	
	// the terrain shadows are only rendered in the last layer(=4) of the shadow maps array
	for (int i = 0; i < gl_in.length(); ++i)
	{
		vec4 position = gl_in[i].gl_Position + vec4(displacement[i],0);
		gl_Layer = 4;
		gl_Position = m_lightViewProjection[ 4 ] * position;
		EmitVertex();
	}
	
	EndPrimitive();
}