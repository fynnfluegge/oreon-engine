#version 430

in vec2 texCoordF;
in vec4 viewSpacePos;
in vec3 position;
in vec3 tangent;

struct Material
{
	sampler2D diffusemap;
	sampler2D normalmap;
	sampler2D heightmap;
	float displaceScale;
	float shininess;
	float emission;
};

struct Fractal
{
	sampler2D normalmap;
	int scaling;
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

layout (std140) uniform DirectionalLight{
	vec3 direction;
	float intensity;
	vec3 ambient;
	vec3 color;
} directional_light;

layout (std140, row_major) uniform LightViewProjections{
	mat4 m_lightViewProjection[6];
	float splitRange[6];
};

uniform sampler2DArray shadowMaps;
uniform Fractal fractals1[7];
uniform sampler2D splatmap;
uniform float scaleY;
uniform float scaleXZ;
uniform Material sand;
uniform Material grass;
uniform Material rock0;
uniform Material rock1;
uniform float sightRangeFactor;
uniform int largeDetailedRange;

const float zfar = 10000;
const float znear = 0.1;
const vec3 fogColor = vec3(0.62,0.85,0.95);

float emission;
float shininess;

float diffuse(vec3 direction, vec3 normal, float intensity)
{
	return max(0.0, dot(normal, -direction) * intensity);
}

float specular(vec3 direction, vec3 normal, vec3 eyePosition, vec3 vertexPosition)
{
	vec3 reflectionVector = normalize(reflect(direction, normal));
	vec3 vertexToEye = normalize(eyePosition - vertexPosition);
	
	float specular = max(0, dot(vertexToEye, reflectionVector));
	
	return pow(specular, shininess) * emission;
}

float linearizeDepth(float depth)
{
	return (2 * znear) / (zfar + znear - depth * (zfar - znear));
}

float varianceShadow(vec3 projCoords, int split, int kernels){
	
	float shadowFactor = 1.0;
	float texelSize = 1.0/ 2048.0;
	float currentDepth = projCoords.z;
	float reduceFactor = 1/ pow(kernels*2+1,2);
	
	for (int i=-kernels; i<=kernels; i++){
		for (int j=-kernels; j<=kernels; j++){
			float shadowMapDepth = texture(shadowMaps, vec3(projCoords.xy,split)
													   + vec3(i,j,0) * texelSize).r; 
			if (currentDepth > shadowMapDepth)
				shadowFactor -= reduceFactor;
		}
	}
	
	return max(0.2,shadowFactor);
}


float shadow(vec3 worldPos)
{
	float shadow = 1;
	float shadowFactor = 0;
	vec3 projCoords = vec3(0,0,0);
	float depth = viewSpacePos.z/zfar;
	if (depth < splitRange[0]){
		vec4 lightSpacePos = m_lightViewProjection[0] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor0 = varianceShadow(projCoords,0,4);
		
		lightSpacePos = m_lightViewProjection[1] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor1 = varianceShadow(projCoords,1,4);
		
		lightSpacePos = m_lightViewProjection[2] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor2 = varianceShadow(projCoords,2,2);
		
		shadowFactor = min(shadowFactor2, min(shadowFactor0,shadowFactor1));
	}
	else if (depth < splitRange[1]){
		vec4 lightSpacePos = m_lightViewProjection[1] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor0 = varianceShadow(projCoords,1,4);
		
		lightSpacePos = m_lightViewProjection[2] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor1 = varianceShadow(projCoords,2,2);
		
		shadowFactor = min(shadowFactor0,shadowFactor1);
	}
	else if (depth < splitRange[2]){
		vec4 lightSpacePos = m_lightViewProjection[2] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,2,2);
	}
	else if (depth < splitRange[3]){
		vec4 lightSpacePos = m_lightViewProjection[3] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,3,2);
	}
	else if (depth < splitRange[4]){
		vec4 lightSpacePos = m_lightViewProjection[4] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,4,1); 
	}
	else if (depth < splitRange[5]){
		vec4 lightSpacePos = m_lightViewProjection[5] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,5,1); 
	}
	else return 1;
	
	return shadowFactor;
}

void main()
{		
	float dist = length(eyePosition - position);
	float height = position.y;
	
	// normalmap/occlusionmap/splatmap coords
	vec2 mapCoords = (position.xz + scaleXZ/2)/scaleXZ; 

	vec3 normal = vec3(0,0,0);
	vec3 bumpNormal = vec3(0,0,0);
	
	normal += (2*(texture(fractals1[0].normalmap, mapCoords*fractals1[0].scaling).rbg)-1);
	normal += (2*(texture(fractals1[1].normalmap, mapCoords*fractals1[1].scaling).rbg)-1);
	normal += (2*(texture(fractals1[2].normalmap, mapCoords*fractals1[2].scaling).rbg)-1);
	normal += (2*(texture(fractals1[3].normalmap, mapCoords*fractals1[3].scaling).rbg)-1);
	normal += (2*(texture(fractals1[4].normalmap, mapCoords*fractals1[4].scaling).rbg)-1);
	normal += (2*(texture(fractals1[5].normalmap, mapCoords*fractals1[5].scaling).rbg)-1);
	normal += (2*(texture(fractals1[6].normalmap, mapCoords*fractals1[6].scaling).rbg)-1);
	normal = normalize(normal);
	bumpNormal = normal;
	
	float rock1Blend  = clamp(height/200,0,1);
	float rock0Blend  = clamp((height+200)/200,0,1) - rock1Blend;
	float sandBlend   = clamp(-height/200,0,1);
	float slopeFactor = 0;
	if (normal.y < 0.95){
		slopeFactor = 1-pow(normal.y+0.05,4);
		rock1Blend += slopeFactor;
		rock1Blend = clamp(rock1Blend,0,1);
		rock0Blend -= slopeFactor;
		rock0Blend = clamp(rock0Blend,0,1);
		sandBlend -= slopeFactor;
		sandBlend = clamp(sandBlend,0,1);
	}
	
	if (dist < largeDetailedRange-20)
	{
		float attenuation = -dist/(largeDetailedRange-20) + 1;
		
		vec3 bitangent = normalize(cross(tangent, normal));
		mat3 TBN = mat3(tangent,normal,bitangent);
		
		bumpNormal = normalize((2*(texture(sand.normalmap, texCoordF).rbg) - 1) * sandBlend
								 +  (2*(texture(rock0.normalmap, texCoordF).rbg) - 1) * rock0Blend
								 +  (2*(texture(rock1.normalmap, texCoordF/4).rbg) - 1) * rock1Blend);
		
		bumpNormal.xz *= attenuation;
		
		bumpNormal = normalize(TBN * bumpNormal);
	}
	
	emission  = sandBlend * sand.emission + rock0Blend * rock0.emission + rock1Blend * rock1.emission;
	shininess = sandBlend * sand.shininess + rock0Blend * rock0.shininess + rock1Blend * rock1.shininess;
	
	float diffuse = diffuse(directional_light.direction, bumpNormal, directional_light.intensity);
	float specular = specular(directional_light.direction, bumpNormal, eyePosition, position);
	vec3 diffuseLight = directional_light.ambient + directional_light.color * diffuse;
	vec3 specularLight = directional_light.color * specular;
	
	vec3 fragColor = mix(texture(sand.diffusemap, texCoordF).rgb, texture(rock0.diffusemap, texCoordF).rgb, rock0Blend);
	
	fragColor = mix(fragColor, texture(rock1.diffusemap,texCoordF/4).rgb, rock1Blend);
	if (normal.y > 0.95){
		fragColor = mix(fragColor,texture(grass.diffusemap,texCoordF).rgb,12*(normal.y-0.95)* clamp(-(height-100)/200,0,1));
	}
	if (normal.y < 0.95){
		fragColor = mix(fragColor,texture(rock1.diffusemap,texCoordF/4).rgb,slopeFactor);
	}
	
	fragColor *= diffuseLight;
	fragColor += specularLight;
	fragColor *= shadow(position);
	
	float fogFactor = -0.0005/sightRangeFactor*(dist-zfar/5*sightRangeFactor);
	
    vec3 rgb = mix(fogColor, fragColor, clamp(fogFactor,0,1));
	
	gl_FragColor = vec4(rgb,1);
}