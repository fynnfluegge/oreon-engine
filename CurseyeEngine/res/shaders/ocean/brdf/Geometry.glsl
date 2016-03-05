#version 430

layout(triangles) in;

layout(triangle_strip, max_vertices = 3) out;
in vec2 texCoordG[];

out vec3 position;
out vec2 texCoordF;
flat out vec3 tangent;

uniform int largeDetailRange;
uniform mat4 projectionViewMatrix;
uniform vec3 eyePosition;
uniform vec4 frustumPlanes[6];
uniform float distortion;
uniform float displacementScale;
uniform sampler2D Dy;
uniform sampler2D Dx;
uniform sampler2D Dz;
uniform float choppiness;

vec3 Tangent;

void calcTangent()
{	
	vec3 v0 = gl_in[0].gl_Position.xyz;
	vec3 v1 = gl_in[1].gl_Position.xyz;
	vec3 v2 = gl_in[2].gl_Position.xyz;

    vec3 e1 = v1 - v0;
    vec3 e2 = v2 - v0;

    float dU1 = texCoordG[1].x - texCoordG[0].x;
    float dV1 = texCoordG[1].y - texCoordG[0].y;
    float dU2 = texCoordG[2].x - texCoordG[0].x;
    float dV2 = texCoordG[2].y - texCoordG[0].y;

    float f = 1.0 / (dU1 * dV2 - dU2 * dV1);

    vec3 t;

    t.x = f * (dV2 * e1.x - dV1 * e2.x);
    t.y = f * (dV2 * e1.y - dV1 * e2.y);
    t.z = f * (dV2 * e1.z - dV1 * e2.z);
	
	Tangent = normalize(t);
}

void main()
{	
	float dx,dy,dz;
	vec4 position0 = gl_in[0].gl_Position;
	vec4 position1 = gl_in[1].gl_Position;
	vec4 position2 = gl_in[2].gl_Position;
	
	float dist = (distance(gl_in[0].gl_Position.xyz, eyePosition) + distance(gl_in[1].gl_Position.xyz, eyePosition) + distance(gl_in[0].gl_Position.xyz, eyePosition))/3;
	// if (dist < largeDetailRange){
		calcTangent();
		
		dy = texture(Dy, texCoordG[0]).r * displacementScale;
		dx = texture(Dx, texCoordG[0]).r * choppiness;
		dz = texture(Dz, texCoordG[0]).r * choppiness;
	
		position0.y -= dy;
		position0.x -= dx;
		position0.z -= dz;
	
		dy = texture(Dy, texCoordG[1]).r * displacementScale;
		dx = texture(Dx, texCoordG[1]).r * choppiness;
		dz = texture(Dz, texCoordG[1]).r * choppiness;
	
		position1.y -= dy;
		position1.x -= dx;
		position1.z -= dz;

		dy = texture(Dy, texCoordG[2]).r * displacementScale;
		dx = texture(Dx, texCoordG[2]).r * choppiness;
		dz = texture(Dz, texCoordG[2]).r * choppiness;
	
		position2.y -= dy;
		position2.x -= dx;
		position2.z -= dz;
		
	// }
	
    gl_Position = projectionViewMatrix * position0;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	texCoordF = texCoordG[0];
	position = position0.xyz;
	tangent = Tangent;
    EmitVertex();
		
	gl_Position = projectionViewMatrix * position1;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	texCoordF = texCoordG[1];
	position = position1.xyz;
	tangent = Tangent;
    EmitVertex();

	gl_Position = projectionViewMatrix * position2;
	gl_ClipDistance[0] = dot(gl_Position ,frustumPlanes[0]);
	gl_ClipDistance[1] = dot(gl_Position ,frustumPlanes[1]);
	gl_ClipDistance[2] = dot(gl_Position ,frustumPlanes[2]);
	gl_ClipDistance[3] = dot(gl_Position ,frustumPlanes[3]);
	gl_ClipDistance[4] = dot(gl_Position ,frustumPlanes[4]);
	gl_ClipDistance[5] = dot(gl_Position ,frustumPlanes[5]);
	texCoordF = texCoordG[2];
	position = position2.xyz;
	tangent = Tangent;
    EmitVertex();
	
    EndPrimitive();	
}