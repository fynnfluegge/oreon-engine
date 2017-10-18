#version 430

layout(triangles) in;

layout(triangle_strip, max_vertices = 3) out;

in vec2 texCoordG[];

struct Fractal
{
	sampler2D normalmap;
	int scaling;
};

struct Material
{
	sampler2D diffusemap;
	sampler2D normalmap;
	sampler2D heightmap;
	float displaceScale;
	float shininess;
	float emission;
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

uniform sampler2D normalmap;
uniform Material sand;
uniform Material rock;
uniform Material cliff;
uniform int largeDetailedRange;
uniform vec4 clipplane;
uniform int isReflection;
uniform int isRefraction;
uniform Fractal fractals1[1];
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

vec4 displacement[3];

void main() {	

	for (int i = 0; i < 3; ++i){
		displacement[i] = vec4(0,0,0,0);
	}

	float dist = (distance(gl_in[0].gl_Position.xyz, eyePosition) + distance(gl_in[1].gl_Position.xyz, eyePosition) + distance(gl_in[2].gl_Position.xyz, eyePosition))/3;
	
	if (dist < (largeDetailedRange - 20) && isReflection == 0){
	
		if (isRefraction == 0){
			for(int k=0; k<gl_in.length(); k++){
				
				vec2 mapCoords = (gl_in[k].gl_Position.xz + scaleXZ/2)/scaleXZ; 
				
				displacement[k] = vec4(0,1,0,0);
				
				float height = gl_in[k].gl_Position.y;
				
				vec3 blendNormal = texture(normalmap, mapCoords).rbg;
				blendNormal = normalize(blendNormal);
					
				float grassBlend = 0;
				float cliffBlend = 0;
				float rockBlend  = clamp((height+200)/200,0,1);
				float sandBlend   = clamp(-height/200,0,1);
				float cliffSlopeFactor = 0;
				
				cliffSlopeFactor = 1-pow(blendNormal.y+0.01,12);
				cliffBlend += cliffSlopeFactor;
				cliffBlend = clamp(cliffBlend,0,1);
				rockBlend -= cliffSlopeFactor;
				rockBlend = clamp(rockBlend,0,1);
				sandBlend -= cliffSlopeFactor;
				sandBlend = clamp(sandBlend,0,1);
				
				// grass Blending
				if (blendNormal.y > 0.95){
					grassBlend = clamp(40*(blendNormal.y-0.95),0,1);
					rockBlend -= grassBlend;
					rockBlend = clamp(rockBlend,0.0,1.0);
					sandBlend -= grassBlend;
					sandBlend = clamp(sandBlend,0.0,1.0);
				}
				
				float scale = texture(sand.heightmap, texCoordG[k]).r * sandBlend * sand.displaceScale
							+ texture(rock.heightmap, texCoordG[k]/20).r * rockBlend * rock.displaceScale
							+ texture(cliff.heightmap, texCoordG[k]/20).r * cliffBlend * cliff.displaceScale;
							
				float attenuation = clamp(-dist/(largeDetailedRange-50) + 1,0.0,1.0);
				scale *= attenuation;

				displacement[k] *= 0;
			}	
		}
		
		calcTangent();
	}
	
	for (int i = 0; i < gl_in.length(); ++i)
	{
		vec4 worldPos = gl_in[i].gl_Position + displacement[i];
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