#version 430

layout(triangles) in;
layout(line_strip, max_vertices = 4) out;

layout (location = 0) in vec2 inUV[];

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

layout (std430, row_major, binding = 1) buffer ssbo {
	mat4 worldMatrix;
	int uvScale;
	int tessFactor;
	float tessSlope;
	float tessShift;
	float displacementScale;
	int highDetailRange;
	float choppiness;
	float kReflection;
	float kRefraction;
	int windowWidth;
	int windowHeight;
	int diffuseEnable;
	float emission;
	float specularFactor;
	float specularAmplifier;
	float reflectionBlendFactor;
	vec3 waterColor;
	float fresnelFactor;
	float capillarStrength;
	float capillarDownsampling;
	float dudvDownsampling;
};

uniform float motion;
uniform vec2 wind;
uniform sampler2D Dy;
uniform sampler2D Dx;
uniform sampler2D Dz;

const int displacementRange = 1000;

void main()
{	
	float dx,dy,dz = 0;
	vec4[] positions = { gl_in[0].gl_Position, gl_in[1].gl_Position, gl_in[2].gl_Position };
	
	float dist = (distance(gl_in[0].gl_Position.xyz, eyePosition) + distance(gl_in[1].gl_Position.xyz, eyePosition) + distance(gl_in[0].gl_Position.xyz, eyePosition))/3;
	if (dist < displacementRange+100)
	{	
		
		for (int i = 0; i < gl_in.length(); ++i)
		{
			dy = texture(Dy, inUV[i]+(wind*motion)).r * max(0,(- distance(gl_in[i].gl_Position.xyz, eyePosition)/displacementRange + 1)) * displacementScale;
			dx = texture(Dx, inUV[i]+(wind*motion)).r * max(0,(- distance(gl_in[i].gl_Position.xyz, eyePosition)/displacementRange + 1)) * choppiness;
			dz = texture(Dz, inUV[i]+(wind*motion)).r * max(0,(- distance(gl_in[i].gl_Position.xyz, eyePosition)/displacementRange + 1)) * choppiness;
	
			positions[i].y += dy;
			positions[i].x -= dx;
			positions[i].z -= dz;
		}
	}
	
	for (int i = 0; i < gl_in.length(); ++i)
	{
		gl_Position = m_ViewProjection * positions[i];
		gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
		gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
		gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
		gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
		gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
		gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
		EmitVertex();
	}	
	
    gl_Position = m_ViewProjection * positions[0];
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
    EmitVertex();
	
    EndPrimitive();
}