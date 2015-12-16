#version 430

in vec2 texCoord4;
in vec3 position0;
in vec3 normal0;
in vec3 tangent;

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
uniform int normalmap;


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
		return pow(reflection, material.shininess) * (material.emission) * texture(material.specularmap, texCoord4).r;
	else
		return pow(reflection, material.shininess) * (material.emission); 
}

void main()
{
	vec3 diffuseLight;
	vec3 specularLight;
	float diffuse;
	float specular;
	vec3 normal;
	
	if (normalmap == 1){
		vec3 Bitangent = normalize(cross(tangent, normal0));
		mat3 TBN = mat3(tangent,normal0,Bitangent);
	
		vec3 bumpNormal = normalize(2*(texture(material.normalmap, texCoord4).rbg)-1);
		
		normal = normalize(TBN * bumpNormal);
	}
	else
		normal = normal0;
	
	
	diffuse = diffuse(directionalLight.direction, normal, directionalLight.intensity);
	
	if (diffuse == 0.0)
		specular = 0.0;
	else
		specular = specular(directionalLight.direction, normal, eyePosition, position0);
	
	diffuseLight = directionalLight.ambient + directionalLight.color * diffuse;
	specularLight = directionalLight.color * specular;
	
	vec3 diffuseColor;
	
	if (diffusemap == 1)
		diffuseColor = texture(material.diffusemap, texCoord4).rgb;
	else
		diffuseColor = material.color;
		
	vec3 rgb = diffuseColor * diffuseLight + specularLight;
	float alpha = texture(material.diffusemap, texCoord4).a;
	
	gl_FragColor = vec4(rgb,alpha);
}