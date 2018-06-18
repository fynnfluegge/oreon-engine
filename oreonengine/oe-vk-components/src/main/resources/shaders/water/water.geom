#version 430

layout(triangles) in;

layout(triangle_strip, max_vertices = 3) out;

layout (location = 0) in vec2 inUV[];

layout (location = 0) out vec3 outPosition;
layout (location = 1) out vec2 outUV;
layout (location = 2) out vec3 outTangent;
layout (location = 3) out vec3 outPositionPreTransform;

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
vec3 tangent;

void calcTangent()
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
	
	tangent = normalize(t);
}

void main()
{	
	float dx,dy,dz;
	vec4 position0 = gl_in[0].gl_Position;
	vec4 position1 = gl_in[1].gl_Position;
	vec4 position2 = gl_in[2].gl_Position;
	
	float dist = (distance(gl_in[0].gl_Position.xyz, eyePosition)
				+ distance(gl_in[1].gl_Position.xyz, eyePosition)
				+ distance(gl_in[0].gl_Position.xyz, eyePosition))/3;
			
	if (dist < displacementRange+100)
	{	
	
		if (dist < constants.highDetailRange){
			calcTangent();
		}
		
		dy = texture(Dy, inUV[0]+(constants.windDirection*ubo.motion)).r
			* max(0,(- distance(gl_in[0].gl_Position.xyz, eyePosition)/displacementRange + 1)) * constants.displacementScale;
		dx = texture(Dx, inUV[0]+(constants.windDirection*ubo.motion)).r
			* max(0,(- distance(gl_in[0].gl_Position.xyz, eyePosition)/displacementRange + 1)) * constants.choppiness;
		dz = texture(Dz, inUV[0]+(constants.windDirection*ubo.motion)).r
			* max(0,(- distance(gl_in[0].gl_Position.xyz, eyePosition)/displacementRange + 1)) * constants.choppiness;
	
		position0.y += dy;
		position0.x -= dx;
		position0.z -= dz;
	
		dy = texture(Dy, inUV[1]+(constants.windDirection*ubo.motion)).r
			* max(0,(- distance(gl_in[1].gl_Position.xyz, eyePosition)/displacementRange + 1)) * constants.displacementScale;
		dx = texture(Dx, inUV[1]+(constants.windDirection*ubo.motion)).r
			* max(0,(- distance(gl_in[1].gl_Position.xyz, eyePosition)/displacementRange + 1)) * constants.choppiness;
		dz = texture(Dz, inUV[1]+(constants.windDirection*ubo.motion)).r
			* max(0,(- distance(gl_in[1].gl_Position.xyz, eyePosition)/displacementRange + 1)) * constants.choppiness;
	
		position1.y += dy;
		position1.x -= dx;
		position1.z -= dz;

		dy = texture(Dy, inUV[2]+(constants.windDirection*ubo.motion)).r
			* max(0,(- distance(gl_in[2].gl_Position.xyz, eyePosition)/displacementRange + 1)) * constants.displacementScale;
		dx = texture(Dx, inUV[2]+(constants.windDirection*ubo.motion)).r
			* max(0,(- distance(gl_in[2].gl_Position.xyz, eyePosition)/displacementRange + 1)) * constants.choppiness;
		dz = texture(Dz, inUV[2]+(constants.windDirection*ubo.motion)).r
			* max(0,(- distance(gl_in[2].gl_Position.xyz, eyePosition)/displacementRange + 1)) * constants.choppiness;
	
		position2.y += dy;
		position2.x -= dx;
		position2.z -= dz;
	}

    gl_Position = m_ViewProjection * position0;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	outUV = inUV[0];
	outPosition = position0.xyz;
	outPositionPreTransform = gl_in[0].gl_Position.xyz;
	outTangent = tangent;
    EmitVertex();
		
	gl_Position = m_ViewProjection * position1;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	outUV = inUV[1];
	outPosition = position1.xyz;
	outPositionPreTransform = gl_in[1].gl_Position.xyz;
	outTangent = tangent;
    EmitVertex();

	gl_Position = m_ViewProjection * position2;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	outUV = inUV[2];
	outPosition = position2.xyz;
	outPositionPreTransform = gl_in[2].gl_Position.xyz;
	outTangent = tangent;
    EmitVertex();
	
    EndPrimitive();	
}
