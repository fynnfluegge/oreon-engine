#version 430

in vec2 texCoord2;
in vec3 position2;
in vec3 normal2;
in vec3 tangent2;
in vec3 bitangent2;

struct DirectionalLight
{
	float intensity;
	vec3 ambient;
	vec3 direction;
	vec3 color;
};

struct Material
{
	vec3 color;
	sampler2D diffusemap;
	sampler2D normalmap;
	sampler2D specularmap;
	float shininess;
	float emission;
};

uniform Material material2;
uniform vec3 eyePosition;
uniform DirectionalLight directionalLight;
uniform int specularmap;
uniform int diffusemap;
uniform mat4 viewMatrix;


float diffuse(vec3 lightDir, vec3 normal, float intensity)
{
	return max(0.0, dot(normal, -lightDir) * intensity);
}

float specular(vec3 lightDir, vec3 normal, vec3 eyeDir)
{
	vec3 reflectionVector = normalize(reflect(lightDir, normal));
	
	float specular = max(0, dot(eyeDir, reflectionVector));
	
	specular = pow(specular, material2.shininess) * material2.emission;
	
	if (specularmap == 1)
		specular *= texture(material2.specularmap, texCoord2).r;
	
	return specular;
}

void main()
{
	vec3 diffuseLight = vec3(0,0,0);
	vec3 specularLight = vec3(0,0,0);
	float diffuseFactor = 0;
	float specularFactor = 0;

	mat3 TBN = (mat3(tangent2, normal2, bitangent2));
	vec3 normal = normalize(2*(texture(material2.normalmap, texCoord2).rbg)-1);
	
	vec3 eyeDirection = normalize(eyePosition - position2);
	
	diffuseFactor = diffuse(directionalLight.direction, TBN * normal, directionalLight.intensity);
	
	specularFactor = specular(directionalLight.direction, TBN * normal, eyeDirection);
	
	diffuseLight = directionalLight.ambient + directionalLight.color * diffuseFactor;
	specularLight = directionalLight.color * specularFactor;
	
	vec3 diffuseColor = vec3(0,0,0);
	
	if (diffusemap == 1)
	{
		diffuseColor = texture(material2.diffusemap, texCoord2).rgb;
	}
	else
		diffuseColor = material2.color;
		
	vec3 rgb = diffuseColor * diffuseLight + specularLight;
	
	gl_FragColor = vec4(rgb,1.0);
}