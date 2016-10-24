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
		return pow(reflection, material2.shininess) * (material2.emission);
	else
		return pow(reflection, material2.shininess) * (material2.emission); 
}

void main()
{
	vec3 diffuseLight;
	vec3 specularLight;
	float diffuse;
	float specular;

	mat3 TBN = transpose(mat3(tangent2, normal2, bitangent2));
	
	vec3 normal = normalize(2*(texture(material2.normalmap, texCoord2).rbg)-1);
	
	vec3 lightdirection_tangentspace = TBN * directionalLight.direction;
	vec3 eye_tangentspace = TBN * eyePosition;
	
	diffuse = diffuse(lightdirection_tangentspace, normal, directionalLight.intensity);
	
	if (diffuse == 0.0)
		specular = 0.0;
	else
		specular = specular(lightdirection_tangentspace, normal, eye_tangentspace, position2);
	
	diffuseLight = directionalLight.ambient + directionalLight.color * diffuse;
	specularLight = directionalLight.color * specular;
	
	vec3 diffuseColor;
	
	if (diffusemap == 1)
	{
		diffuseColor = texture(material2.diffusemap, texCoord2).rgb;
		
		if (specularmap == 1)
			diffuseColor += texture(material2.specularmap, texCoord2).rgb;
	}
	else
		diffuseColor = material2.color;
		
	vec3 rgb = diffuseColor * diffuseLight + specularLight;
	
	gl_FragColor = vec4(rgb,1.0);
}