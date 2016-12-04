#version 430

in vec2 texCoord2;
in vec3 position2;
in vec3 normal2;

struct DirectionalLight
{
	float intensity;
	vec3 ambient;
	vec3 direction;
	vec3 color;
};

struct Material
{
	sampler2D diffusemap;
	float shininess;
	float emission;
};

uniform Material material;
uniform vec3 eyePosition;
uniform DirectionalLight directionalLight;
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
	
	float dist = length(eyePosition - position2);

	
	vec3 eyeDirection = normalize(eyePosition - position2);
	
	diffuseFactor = diffuse(directionalLight.direction, normal2, directionalLight.intensity);
	
	specularFactor = specular(directionalLight.direction, normal2, eyeDirection);
	
	diffuseLight = directionalLight.ambient + directionalLight.color * diffuseFactor;
	specularLight = directionalLight.color * specularFactor;
	
	vec3 diffuseColor = texture(material.diffusemap, texCoord2).rgb;
	
		
	vec3 fragColor = diffuseColor * diffuseLight;// + specularLight;
	
	float fogFactor = -0.0005/sightRangeFactor*(dist-zFar/5*sightRangeFactor);
	
    vec3 rgb = mix(fogColor, fragColor, clamp(fogFactor,0,1));
	
	gl_FragColor = vec4(rgb,1);
	
	gl_FragColor = vec4(rgb,1.0);
}