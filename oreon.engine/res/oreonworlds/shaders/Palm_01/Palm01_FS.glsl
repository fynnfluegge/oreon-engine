#version 330

in vec3 normal_FS;
in vec3 position_FS;

struct Material
{
	vec3 color;
	float shininess;
	float emission;
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 viewProjectionMatrix;
	vec4 frustumPlanes[6];
};

layout (std140) uniform DirectionalLight{
	vec3 direction;
	float intensity;
	vec3 ambient;
	vec3 color;
} directional_light;
	
uniform Material material;
uniform float sightRangeFactor;

const float zFar = 10000;
const vec3 fogColor = vec3(0.62,0.85,0.95);


float diffuse(vec3 direction, vec3 normal, float intensity)
{
	return max(0.0, dot(normal, -direction) * intensity);
}

float specular(vec3 direction, vec3 normal, vec3 eyePosition, vec3 vertexPosition)
{
	vec3 reflectionVector = normalize(reflect(-direction, normal));
	vec3 vertexToEye = normalize(eyePosition - vertexPosition);
	
	float reflection = dot(vertexToEye, reflectionVector);
	
	return pow(reflection, material.shininess) * material.emission;
}

void main()
{	
	float dist = length(eyePosition - position_FS);

	float diffuseFactor = diffuse(directional_light.direction, normal_FS, directional_light.intensity);
	float specularFactor = 0;
	
	if (diffuseFactor > 0.0)
		specularFactor = specular(directional_light.direction, normal_FS, eyePosition, position_FS);
	
	vec3 diffuseLight = directional_light.color * diffuseFactor;
	vec3 specularLight = directional_light.color * specularFactor;
	
	vec3 fragColor = material.color * (directional_light.ambient + diffuseLight);// + specularLight;
	
	float fogFactor = -0.0005/sightRangeFactor*(dist-zFar/5*sightRangeFactor);
	
    vec3 rgb = mix(fogColor, fragColor, clamp(fogFactor,0,1));
	
	gl_FragColor = vec4(rgb,1.0);
}