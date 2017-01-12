#version 430

in vec2 texCoord_FS;
in vec3 position_FS;
in vec3 normal_FS;
in vec3 tangent_FS;
in vec3 bitangent_FS;

struct Material
{
	sampler2D diffusemap;
	sampler2D normalmap;
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

uniform Material material;
uniform float sightRangeFactor;

const float zFar = 10000;
const vec3 fogColor = vec3(0.62,0.85,0.95);


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

void main()
{
	vec3 diffuseLight = vec3(0,0,0);
	vec3 specularLight = vec3(0,0,0);
	float diffuseFactor = 0;
	float specularFactor = 0;
	
	float dist = length(eyePosition - position_FS);

	mat3 TBN = mat3(tangent_FS, normal_FS, bitangent_FS);
	vec3 normal = normalize(2*(texture(material.normalmap, texCoord_FS*6).rbg)-1);
	
	vec3 eyeDirection = normalize(eyePosition - position_FS);
	
	diffuseFactor = diffuse(directional_light.direction, normalize(TBN * normal), directional_light.intensity);
	
	specularFactor = specular(directional_light.direction, normalize(TBN * normal), eyeDirection);
	
	diffuseLight = directional_light.ambient + directional_light.color * diffuseFactor;
	specularLight = directional_light.color * specularFactor;
	
	vec3 diffuseColor = texture(material.diffusemap, texCoord_FS*6).rgb;
	
		
	vec3 fragColor = diffuseColor * diffuseLight + specularLight;
	
	float fogFactor = -0.0005/sightRangeFactor*(dist-zFar/5*sightRangeFactor);
	
    vec3 rgb = mix(fogColor, fragColor, clamp(fogFactor,0,1));
	
	gl_FragColor = vec4(rgb,1.0);
}