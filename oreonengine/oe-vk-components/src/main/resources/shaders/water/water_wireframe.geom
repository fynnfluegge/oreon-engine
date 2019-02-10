#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(triangles) in;
layout(line_strip, max_vertices = 4) out;

layout (location = 0) in vec2 inUV[];

layout(set = 0, binding = 0, std140, row_major) uniform Camera {
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

layout (push_constant, std430, row_major) uniform Constants{
	mat4 m_World;
	vec2 windDirection;
	float tessSlope;
	float tessShift;
	int tessFactor;
	int uvScale;
	float displacementScale;
	float choppiness;
	int highDetailRange;
	float kReflection;
	float kRefraction;
	int windowWidth;
	int windowHeight;
	float emission;
	float specular;
} constants;

layout(set = 1, binding = 0) uniform sampler2D Dy;
layout(set = 1, binding = 1) uniform sampler2D Dx;
layout(set = 1, binding = 2) uniform sampler2D Dz;

layout(set = 1, binding = 7) uniform UBO {
	float motion;
	float distortion;
} ubo;

int displacementRange = 1000;

void main()
{	
	float dx,dy,dz;

	dy = texture(Dy, inUV[0]).r * constants.displacementScale;
	dx = texture(Dx, inUV[0]).r * constants.choppiness;
	dz = texture(Dz, inUV[0]).r * constants.choppiness;
	vec4 position0 = gl_in[0].gl_Position;
	position0.y += dy;
	position0.x -= dx;
	position0.z -= dz;
    gl_Position = m_ViewProjection * position0;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
    EmitVertex();
	
	dy = texture(Dy, inUV[1]).r * constants.displacementScale;
	dx = texture(Dx, inUV[1]).r * constants.choppiness;
	dz = texture(Dz, inUV[1]).r * constants.choppiness;
	vec4 position1 = gl_in[1].gl_Position;
	position1.y += dy;
	position1.x -= dx;
	position1.z -= dz;
	gl_Position = m_ViewProjection * position1;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
    EmitVertex();

	dy = texture(Dy, inUV[2]).r * constants.displacementScale;
	dx = texture(Dx, inUV[2]).r * constants.choppiness;
	dz = texture(Dz, inUV[2]).r * constants.choppiness;
	vec4 position2 = gl_in[2].gl_Position;
	position2.y += dy;
	position2.x -= dx;
	position2.z -= dz;
	gl_Position = m_ViewProjection * position2;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
    EmitVertex();
	
    gl_Position = m_ViewProjection * position0;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
    EmitVertex();
	
    EndPrimitive();
}