#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(triangles) in;

layout(line_strip, max_vertices = 4) out;

layout (location = 0) in vec2 inUV[];

layout (location = 0) out vec3 outPosition;
layout (location = 1) out vec2 outUV;
layout (location = 2) out vec3 outTangent;

layout(set = 0, binding = 0, std140, row_major) uniform Camera {
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

layout (push_constant, std430, row_major) uniform Constants{
	mat4 localMatrix;
	mat4 worldMatrix;
	float verticalScaling;
	float horizontalScaling;
	int lod;
	float gap;
	vec2 location;
	vec2 index;
	int lod_morph_area[8];
	int tessFactor;
	float tessSlope;
	float tessShift;
	float uvScaling;
	int highDetailRange;
} constants;

vec3 tangent;

void calcTangent()
{	
	vec3 v0 = gl_in[0].gl_Position.xyz;
	vec3 v1 = gl_in[1].gl_Position.xyz;
	vec3 v2 = gl_in[2].gl_Position.xyz;

	// edges of the face/triangle
    vec3 e1 = v1 - v0;
    vec3 e2 = v2 - v0;
	
	vec2 uv0 = inUV[0];
	vec2 uv1 = inUV[1];
	vec2 uv2 = inUV[2];

    vec2 deltaUV1 = uv1 - uv0;
	vec2 deltaUV2 = uv2 - uv0;
	
	float r = 1.0 / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);
	
	tangent = normalize((e1 * deltaUV2.y - e2 * deltaUV1.y)*r);
}

vec3 displacement[3];

void main() {	

	for (int i = 0; i < 3; ++i){
		displacement[i] = vec3(0,0,0);
	}

	float dist = (distance(gl_in[0].gl_Position.xyz, eyePosition) + distance(gl_in[1].gl_Position.xyz, eyePosition)
		+ distance(gl_in[2].gl_Position.xyz, eyePosition))/3;
	
	if (dist < (constants.highDetailRange)){
	
		calcTangent();
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
		outUV = inUV[i];
		outPosition = (worldPos).xyz;
		outTangent = tangent;
		EmitVertex();
	}
	
	vec4 worldPos = gl_in[0].gl_Position + vec4(displacement[0],0);
	gl_Position = m_ViewProjection * worldPos;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	outUV = inUV[0];
	outPosition = (worldPos).xyz;
	outTangent = tangent;
	EmitVertex();
	
	EndPrimitive();
}