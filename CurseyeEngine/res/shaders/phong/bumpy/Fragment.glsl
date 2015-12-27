#version 430

in vec2 texCoord2;
in vec3 position2;
in vec3 normal2;
flat in vec3 tangent;

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

uniform vec3 eyePosition;
uniform DirectionalLight directionalLight;
uniform Material material;
uniform int specularmap;
uniform int diffusemap;


float diffuse(vec3 direction, vec3 normal, float intensity)
{
	return max(0.0, dot(normal, -direction) * intensity);
}

float specular(vec3 direction, vec3 normal, vec3 eyePosition, vec3 vertexPosition)
{
	vec3 reflectionVector = normalize(reflect(-direction, normal));
	vec3 vertexToEye = normalize(eyePosition - vertexPosition);
	
	float reflection = dot(vertexToEye, reflectionVector);
	
	if(specularmap == 1)
		return pow(reflection, material.shininess) * (material.emission) * texture(material.specularmap, texCoord2).r;
	else
		return pow(reflection, material.shininess) * (material.emission); 
}

void main()
{
	vec3 diffuseLight;
	vec3 specularLight;
	float diffuse;
	float specular;
	
	vec3 Bitangent = normalize(cross(tangent, normal2));
	mat3 TBN = mat3(tangent,normal2,Bitangent);
	
	vec3 bumpNormal = normalize(2*(texture(material.normalmap, texCoord2).rbg)-1);
		
	vec3 normal = normalize(TBN * bumpNormal);
	
	diffuse = diffuse(directionalLight.direction, normal, directionalLight.intensity);
	
	if (diffuse == 0.0)
		specular = 0.0;
	else
		specular = specular(directionalLight.direction, normal, eyePosition, position2);
	
	diffuseLight = directionalLight.ambient + directionalLight.color * diffuse;
	specularLight = directionalLight.color * specular;
	
	vec3 diffuseColor;
	
	if (diffusemap == 1)
		diffuseColor = texture(material.diffusemap, texCoord2).rgb;
	else
		diffuseColor = material.color;
		
	vec3 rgb = diffuseColor * diffuseLight + specularLight;
	
	gl_FragColor = vec4(rgb,1.0);
}