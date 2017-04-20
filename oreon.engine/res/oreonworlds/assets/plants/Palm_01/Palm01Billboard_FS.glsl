#version 430

in vec3 position_FS;
in vec2 texCoord_FS;

struct Material
{
	sampler2D diffusemap;
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
const float zNear = 0.1;
const vec3 fogColor = vec3(0.62,0.85,0.95);

float diffuse(vec3 direction, vec3 normal, float intensity)
{
	return max(0.2, dot(normal, -direction) * intensity);
}

float alphaDistanceFactor(float dist)
{
	return 0.01f * (dist-120);
}

void main()
{	
	float dist = length(eyePosition - position_FS);

	float diffuseFactor = diffuse(directional_light.direction, vec3(0,1,0), directional_light.intensity);
	
	vec3 diffuseLight = directional_light.color * diffuseFactor;
	
	vec3 fragColor = texture(material.diffusemap, texCoord_FS).rgb * (directional_light.ambient + diffuseLight);
	
	float fogFactor = -0.0005/sightRangeFactor*(dist-zFar/5*sightRangeFactor);
	
    vec3 rgb = mix(fogColor, fragColor, clamp(fogFactor,0,1));
	
	float alpha = texture(material.diffusemap, texCoord_FS).a;
	alpha *= alphaDistanceFactor(dist);

	gl_FragColor = vec4(rgb,alpha);
}