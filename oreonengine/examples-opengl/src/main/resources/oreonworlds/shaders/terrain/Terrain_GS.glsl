#version 430

layout(triangles) in;

layout(triangle_strip, max_vertices = 3) out;

in vec2 texCoordG[];

struct Material
{
	sampler2D diffusemap;
	sampler2D normalmap;
	sampler2D heightmap;
	float heightScaling;
	float horizontalScaling;
};

out vec2 texCoordF;
out vec4 viewSpacePos;
out vec3 position;
out vec3 tangent;

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

uniform sampler2D splatmap;
uniform Material materials[3];
uniform int largeDetailRange;
uniform vec4 clipplane;
uniform float scaleXZ;

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

vec3 displacement[3];

void main() {	

	for (int i = 0; i < 3; ++i){
		displacement[i] = vec3(0,0,0);
	}

	float dist = (distance(gl_in[0].gl_Position.xyz, eyePosition) + distance(gl_in[1].gl_Position.xyz, eyePosition) + distance(gl_in[2].gl_Position.xyz, eyePosition))/3;
	
	if (dist < (largeDetailRange)){
	
		calcTangent();
		
		for(int k=0; k<gl_in.length(); k++){
			
			vec2 mapCoords = (gl_in[k].gl_Position.xz + scaleXZ/2)/scaleXZ; 
			
			vec4 v_splatmap = texture(splatmap, mapCoords).rgba;
			float[4] blendValues = float[](v_splatmap.r,v_splatmap.g,v_splatmap.b,v_splatmap.a);
			
			displacement[k] = vec3(0,1,0);
			
			float height = gl_in[k].gl_Position.y;
			
			float scale = 0;
			for (int i=0; i<3; i++){
				scale += texture(materials[i].heightmap, texCoordG[k]/materials[i].horizontalScaling).r * materials[i].heightScaling * blendValues[i];
			}
						
			float attenuation = clamp(- distance(gl_in[k].gl_Position.xyz, eyePosition)/(largeDetailRange-50) + 1,0.0,1.0);
			scale *= attenuation;

			displacement[k] *= scale;
		}	
	}
	
	for (int i = 0; i < gl_in.length(); ++i)
	{
		vec4 worldPos = gl_in[i].gl_Position + vec4(displacement[i],0);
		gl_Position = m_ViewProjection * worldPos;
		gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
		gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
		gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
		gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
		gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
		gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
		gl_ClipDistance[6] = dot(worldPos ,clipplane);
		texCoordF = texCoordG[i];
		viewSpacePos = m_View * worldPos;
		position = (worldPos).xyz;
		tangent = Tangent;
		EmitVertex();
	}
	EndPrimitive();
}