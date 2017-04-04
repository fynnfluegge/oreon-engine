#version 330

in vec3 position_FS;
in vec2 texCoord_FS;
in vec3 normal_FS;
in vec4 viewSpacePos;

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

layout (std140, row_major) uniform LightViewProjections{
	mat4 m_lightViewProjection[6];
	float splitRange[6];
};
	
uniform sampler2DArray shadowMaps;
uniform Material material;
uniform float sightRangeFactor;

const float zFar = 10000;
const float zNear = 0.1;
const vec3 fogColor = vec3(0.62,0.85,0.95);

float linearize(float depth)
{
	return (2 * zNear) / (zFar + zNear - depth * (zFar - zNear));
}

float diffuse(vec3 direction, vec3 normal, float intensity)
{
	return max(0.0, dot(normal, -direction) * intensity);
}

float varianceShadow(vec3 projCoords, int split){
	
	float shadowFactor = 1.0;
	float texelSize = 1.0/ 4096.0;
	float currentDepth = projCoords.z;
	
	for (int i=-1; i<=1; i++){
		for (int j=-1; j<=1; j++){
			float shadowMapDepth = texture(shadowMaps, vec3(projCoords.xy,split)
													   + vec3(i,j,0) * texelSize).r; 
			if (linearize(currentDepth) > linearize(shadowMapDepth) + 0.00002)
				shadowFactor -= 0.1;
		}
	}

	shadowFactor = max(0.1, shadowFactor);
	
	return shadowFactor;
}

float shadow(vec3 worldPos)
{
	float shadowFactor = 1;
	vec3 projCoords = vec3(0,0,0);
	float depth = viewSpacePos.z/zFar;
	if (depth < splitRange[0]){
		vec4 lightSpacePos = m_lightViewProjection[0] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor0 = varianceShadow(projCoords,0);
		
		lightSpacePos = m_lightViewProjection[1] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor1 = varianceShadow(projCoords,1);
		
		lightSpacePos = m_lightViewProjection[2] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor2 = varianceShadow(projCoords,2);
		
		shadowFactor = min(shadowFactor2, min(shadowFactor0,shadowFactor1));
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

	return shadowFactor;
}

void main()
{	
	float dist = length(eyePosition - position_FS);

	float diffuseFactor = diffuse(directional_light.direction, vec3(0,1,0), directional_light.intensity);
	
	vec3 diffuseLight = directional_light.color * diffuseFactor;
	
	vec3 fragColor = texture(material.diffusemap, texCoord_FS).rgb * (directional_light.ambient + diffuseLight);
	fragColor *= shadow(position_FS);
	
	float alpha = texture(material.diffusemap, texCoord_FS).a;
	
	float fogFactor = -0.0005/sightRangeFactor*(dist-zFar/5*sightRangeFactor);
	
    vec3 rgb = mix(fogColor, fragColor, clamp(fogFactor,0,1));

	gl_FragColor = vec4(rgb,alpha);
}