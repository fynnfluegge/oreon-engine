#version 430
#extension GL_EXT_texture_array : enable

in vec3 position;
in vec4 viewSpacePos;
in vec3 tangent;

struct Fractal
{
	sampler2D heightmap;
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
	mat4 m_lightViewProjection[4];
	float splitRange[4];
	float shadowMapArrayIndices[4];
};

uniform sampler2DArray shadowMaps;
uniform Fractal fractals1[10];
uniform float scaleY;
uniform float scaleXZ;
uniform float sightRangeFactor;
uniform int largeDetailedRange;

const float zFar = 10000;
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

float shadow(vec3 worldPos)
{
	float shadow = 1;
	float shadowMapDepth = 0;
	vec3 projCoords = vec3(0,0,0);
	float depth = viewSpacePos.z/zFar;
	if (depth < splitRange[0]){
		vec4 lightSpacePos = m_lightViewProjection[0] * vec4(worldPos,1.0);
		// Note if lightViewProjSpace is perspective dont't forget lightSpacePos.xyz/lightSpacePos.w
		// Due to orthographic projection, it's not necessary for directional lights
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowMapDepth = texture(shadowMaps, vec3(projCoords.xy,shadowMapArrayIndices[0])).r; 
	}
	else if (depth < splitRange[1]){
		vec4 lightSpacePos = m_lightViewProjection[1] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowMapDepth = texture(shadowMaps, vec3(projCoords.xy,shadowMapArrayIndices[1])).r;  
	}
	else if (depth < splitRange[2]){
		vec4 lightSpacePos = m_lightViewProjection[2] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowMapDepth = texture(shadowMaps, vec3(projCoords.xy,shadowMapArrayIndices[2])).r; 
	}
	else if (depth < splitRange[3]){
		vec4 lightSpacePos = m_lightViewProjection[3] * vec4(worldPos,1.0);
		vec3 projCoords = lightSpacePos.xyz;
		shadowMapDepth = texture(shadowMaps, vec3(projCoords.xy,shadowMapArrayIndices[3])).r; 
	}
	else return 1;
	
	float currentDepth = projCoords.z;  
	shadow = currentDepth > shadowMapDepth ? 0.0 : 1.0; 
	return shadow;
}

void main()
{		
	float dist = length(eyePosition - position);
	float height = position.y;
	
	// normalmap/occlusionmap/splatmap coords
	vec2 mapCoords = (position.xz + scaleXZ/2)/scaleXZ;  
	
	vec3 normal = vec3(0,0,0);
	
	normal += (2*(texture(fractals1[0].normalmap, mapCoords*fractals1[0].scaling).rbg)-1);
	normal += (2*(texture(fractals1[1].normalmap, mapCoords*fractals1[1].scaling).rbg)-1);
	normal += (2*(texture(fractals1[2].normalmap, mapCoords*fractals1[2].scaling).rbg)-1);
	normal += (2*(texture(fractals1[3].normalmap, mapCoords*fractals1[3].scaling).rbg)-1);
	normal += (2*(texture(fractals1[4].normalmap, mapCoords*fractals1[4].scaling).rbg)-1);
	normal += (2*(texture(fractals1[5].normalmap, mapCoords*fractals1[5].scaling).rbg)-1);
	normal += (2*(texture(fractals1[6].normalmap, mapCoords*fractals1[6].scaling).rbg)-1);
	normal = normalize(normal);
	
	if (dist < largeDetailedRange-20)
	{
		float attenuation = -dist/(largeDetailedRange-20) + 1;
		vec3 bitangent = normalize(cross(tangent, normal));
		mat3 TBN = mat3(tangent,normal,bitangent);
		
		vec3 bumpNormal =   normalize(
							 (2*(texture(fractals1[7].normalmap, mapCoords*fractals1[7].scaling).rbg)-1)
							+(2*(texture(fractals1[8].normalmap, mapCoords*fractals1[8].scaling).rbg)-1)
							+(2*(texture(fractals1[9].normalmap, mapCoords*fractals1[9].scaling).rbg)-1));

		bumpNormal.xz *= attenuation;
		
		normal = normalize(TBN * bumpNormal);
	}
	
	vec3 diffuseLight = vec3(0.0);
	vec3 specularLight = vec3(0.0);
	float diffuseFactor = 0.0;
	float specularFactor = 0.0;
	
	emission = 0;
	shininess = 0;
	
	vec3 grass = vec3(0.123,0.163,0.04);
	vec3 rock = vec3(0.2,0.2,0.2);
	vec3 darkRock = vec3(0.02,0.02,0.02);
	vec3 sand = vec3(0.1,0.066,0.032);
	vec3 snow = vec3(1,1,1);
	
	diffuseFactor = diffuse(directional_light.direction, normal, directional_light.intensity);
	specularFactor = specular(directional_light.direction, normal, eyePosition, position);
	diffuseLight = directional_light.ambient + directional_light.color * diffuseFactor;
	specularLight = directional_light.color * specularFactor;
	
	vec3 fragColor;
	vec3 sandrock = mix(sand,rock, clamp(height/(scaleY/2)+0.2,0,1));
	vec3 sandrocksnow = mix(sandrock,snow, clamp((height-scaleY/4)/(scaleY/2),0,1));
	fragColor = mix(darkRock,sandrocksnow, clamp((height+scaleY/2)/(scaleY/4),0,1));
	float grassFactor = clamp(height/(scaleY*4)+0.95,0.9,1.0);
	if (normal.y > grassFactor){
		fragColor = mix(grass,fragColor,(1-normal.y)*10);
	}
	
	fragColor *= diffuseLight;
	fragColor += specularLight;
	fragColor *= shadow(position);
	
	float fogFactor = -0.0005/sightRangeFactor*(dist-zFar/5*sightRangeFactor);
	
    vec3 rgb = mix(fogColor, fragColor, clamp(fogFactor,0,1));
	
	gl_FragColor = vec4(rgb,1);
}