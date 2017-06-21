#version 430

in vec2 texCoord_FS;
in vec3 position_FS;
in vec3 normal_FS;
in vec4 viewSpacePos;

struct Material
{
	sampler2D diffusemap;
	float shininess;
	float emission;
};

layout (std140) uniform DirectionalLight{
	vec3 direction;
	float intensity;
	vec3 ambient;
	vec3 color;
} directional_light;

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 viewProjectionMatrix;
	vec4 frustumPlanes[6];
};

layout (std140, row_major) uniform LightViewProjections{
	mat4 m_lightViewProjection[6];
	float splitRange[6];
};

uniform sampler2DArray shadowMaps;
uniform Material material;
uniform float sightRangeFactor;

const float zFar = 10000.0;
const float zNear = 0.1;
const vec3 fogColor = vec3(0.62,0.85,0.95);

float linearize(float depth)
{
	return (2 * zNear) / (zFar + zNear - depth * (zFar - zNear));
}

float diffuse(vec3 lightDir, vec3 normal, float intensity)
{
	return max(0.0, dot(normal, -lightDir) * intensity);
}

float specular(vec3 lightDir, vec3 normal, vec3 eyeDir)
{
	vec3 reflectionVector = normalize(reflect(lightDir, normal));
	
	float specular = max(0, dot(eyeDir, reflectionVector));
	
	specular = pow(specular, material.shininess) * material.emission;
	
	return specular;
}

float varianceShadow(vec3 projCoords, int split){
	
	float shadowFactor = 1.0;
	float texelSize = 1.0/ 2048.0;
	float currentDepth = projCoords.z;
	
	for (int i=-1; i<=1; i++){
		for (int j=-1; j<=1; j++){
			float shadowMapDepth = texture(shadowMaps, vec3(projCoords.xy,split)
													   + vec3(i,j,0) * texelSize).r; 
			if (linearize(currentDepth) > linearize(shadowMapDepth) + 0.00001)
				shadowFactor -= 0.1;
		}
	}
	
	return shadowFactor;
}

float shadow(vec3 worldPos)
{
	float shadow = 1;
	float shadowFactor = 0;
	vec3 projCoords = vec3(0,0,0);
	float depth = viewSpacePos.z/zFar;
	if (depth < splitRange[0]){
		vec4 lightSpacePos = m_lightViewProjection[0] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,0);
	}
	else if (depth < splitRange[1]){
		vec4 lightSpacePos = m_lightViewProjection[1] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,1);
	}
	else if (depth < splitRange[2]){
		vec4 lightSpacePos = m_lightViewProjection[2] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,2);
	}
	else if (depth < splitRange[3]){
		vec4 lightSpacePos = m_lightViewProjection[3] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,3);
	}
	else if (depth < splitRange[4]){
		vec4 lightSpacePos = m_lightViewProjection[4] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,4); 
	}
	else if (depth < splitRange[5]){
		vec4 lightSpacePos = m_lightViewProjection[5] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,5); 
	}
	else return 1;

	return shadowFactor;
}

float alphaDistanceFactor(float dist)
{
	return clamp(0.005f * (-dist+400),0,1);
}

void main()
{
	vec3 diffuseLight = vec3(0,0,0);
	vec3 specularLight = vec3(0,0,0);
	float diffuseFactor = 0;
	float specularFactor = 0;
	
	float dist = length(eyePosition - position_FS);

	vec3 eyeDirection = normalize(eyePosition - position_FS);
	
	diffuseFactor = diffuse(directional_light.direction, normal_FS, directional_light.intensity);
	
	specularFactor = specular(directional_light.direction, normal_FS, eyeDirection);
	
	diffuseLight = directional_light.ambient + directional_light.color * diffuseFactor;
	specularLight = directional_light.color * specularFactor;
	
	vec3 diffuseColor = texture(material.diffusemap, texCoord_FS).rgb;
	
		
	vec3 fragColor = diffuseColor * diffuseLight;// + specularLight;
	
	// prevent shadows on faces backfacing lightsource
	// if (dot(normal_FS, -directional_light.direction) > 0.0)
		// fragColor *= shadow(position_FS);
	
	float fogFactor = -0.0005/sightRangeFactor*(dist-zFar/5*sightRangeFactor);
	
    vec3 rgb = mix(fogColor, fragColor, clamp(fogFactor,0,1));
	
	float alpha = texture(material.diffusemap, texCoord_FS).a;
	alpha *= alphaDistanceFactor(dist);
	
	gl_FragColor = vec4(rgb,alpha);
}