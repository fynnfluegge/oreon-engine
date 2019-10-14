#version 430
#extension GL_ARB_separate_shader_objects : enable

layout(triangles) in;

layout(triangle_strip, max_vertices = 3) out;

layout (location = 0) in vec2 inUV[];

layout (location = 0) out vec3 outPos;
layout (location = 1) out vec2 outUV;
layout (location = 2) out vec3 outTangent;

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

vec3 calcTangent()
{	
	vec3 v0 = gl_in[0].gl_Position.xyz;
	vec3 v1 = gl_in[1].gl_Position.xyz;
	vec3 v2 = gl_in[2].gl_Position.xyz;

    vec3 e1 = v1 - v0;
    vec3 e2 = v2 - v0;

    float dU1 = inUV[1].x - inUV[0].x;
    float dV1 = inUV[1].y - inUV[0].y;
    float dU2 = inUV[2].x - inUV[0].x;
    float dV2 = inUV[2].y - inUV[0].y;

    float f = 1.0 / (dU1 * dV2 - dU2 * dV1);

    vec3 t;

    t.x = f * (dV2 * e1.x - dV1 * e2.x);
    t.y = f * (dV2 * e1.y - dV1 * e2.y);
    t.z = f * (dV2 * e1.z - dV1 * e2.z);
	
	t = normalize(t);
	return t;
}

void main()
{	
	float dx,dy,dz = 0;
	vec4[] positions = { gl_in[0].gl_Position, gl_in[1].gl_Position, gl_in[2].gl_Position };
	vec3 tangent = vec3(0);
	
	float dist = (distance(gl_in[0].gl_Position.xyz, eyePosition) + distance(gl_in[1].gl_Position.xyz, eyePosition) + distance(gl_in[0].gl_Position.xyz, eyePosition))/3;
	if (dist < displacementRange+100)
	{	
		if (dist < highDetailRange){
			tangent = calcTangent();
		}
		
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
		outUV = inUV[i];
		outPos = positions[i].xyz;
		outTangent = tangent;
		EmitVertex();
	}
}
