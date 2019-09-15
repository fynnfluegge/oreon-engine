#version 430
#extension GL_ARB_separate_shader_objects : enable
#define M_PI 3.1415926535897932384626433832795

in vec3 position_FS;
in vec2 texCoord_FS;
in vec3 tangent;

layout (location = 0) in vec3 inPosition;
layout (location = 1) in vec2 inUV;
layout (location = 2) in vec3 inTangent;

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 worldPosition_out;
layout(location = 2) out vec4 normal_out;
layout(location = 3) out vec4 specular_emission_diffuse_ssao_bloom_out;
layout(location = 4) out vec4 lightScattering_out;

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

layout (std140) uniform DirectionalLight{
	vec3 direction;
	float intensity;
	vec3 ambient;
	vec3 color;
} directional_light;

layout (std430, row_major, binding = 1) buffer ssbo {
	mat4 worldMatrix;
	int uvScale;
	int tessFactor;
	float tessSlope;
	float tessShift;
	float displacementScale;
	int largeDetailRange;
	float choppiness;
	float kReflection;
	float kRefraction;
	int windowWidth;
	int windowHeight;
	int diffuseEnable;
	float emission;
	float specularFactor;
	float specularAmplifier;
	float reflectionBlendFactor;
	vec3 waterColor;
	float fresnelFactor;
	float capillarStrength;
	float capillarDownsampling;
	float dudvDownsampling;
};

uniform sampler2D waterReflection;
uniform sampler2D waterRefraction;
uniform sampler2D dudvMap;
uniform float distortion;
uniform float motion;
uniform sampler2D normalmap;
uniform int isCameraUnderWater;
uniform vec2 wind;

const float Eta = 0.15;
const float zfar = 10000;
const float znear = 0.1;

float fresnelApproximated(vec3 normal, vec3 vertexToEye)
{
	vec3 halfDirection = normalize(normal + vertexToEye);
    
    float cosine = dot(halfDirection, vertexToEye);

	float fresnel = Eta + (1.0 - Eta) * pow(max(0.0, 1.0 - dot(vertexToEye, normal)), fresnelFactor);
	
	return clamp(pow(fresnel, 1.0),0.0,1.0);
}

float specularReflection(vec3 direction, vec3 normal, float specularFactor, float emissionFactor, vec3 vertexToEye)
{
	vec3 reflectionVector = normalize(reflect(direction, normal));
	
	float specular = max(0.0, dot(vertexToEye, reflectionVector));
	
	return pow(specular, specularFactor) * emissionFactor;
}
 
void main(void)
{
	vec3 vertexToEye = normalize(eyePosition - inPosition);
	float dist = length(eyePosition - inPosition);
	
	vec2 waveMotion = wind * vec2(motion);

	// normal
	vec3 normal = texture(normalmap, inUV + waveMotion).rgb;

	normal = normalize(normal);
	
	float fresnel = fresnelApproximated(normal.xzy, vertexToEye);
	
	if (dist < largeDetailRange-50.0){
		float attenuation = clamp(-dist/(largeDetailRange-50) + 1,0.0,1.0);
		vec3 bitangent = normalize(cross(inTangent, normal));
		mat3 TBN = mat3(inTangent,bitangent,normal);
		vec3 bumpNormal = texture(normalmap, inUV * capillarDownsampling + waveMotion).rgb;
		bumpNormal.z *= capillarStrength;
		bumpNormal.xy *= attenuation;
		bumpNormal = normalize(bumpNormal);
		normal = normalize(TBN * bumpNormal);
	}
	
	// projCoord //
	vec3 dudvCoord = normalize((2 * texture(dudvMap, inUV * dudvDownsampling + distortion).rbg) - 1);
	vec2 projCoord = vec2(gl_FragCoord.x/windowWidth, gl_FragCoord.y/windowHeight);
 
    // Reflection //
	vec2 reflecCoords = projCoord.xy + dudvCoord.rb * kReflection;
	reflecCoords = clamp(reflecCoords, kReflection, 1-kReflection);
    vec3 reflection = mix(texture(waterReflection, reflecCoords).rgb, waterColor, reflectionBlendFactor);
    reflection *= fresnel;
 
    // Refraction //
	vec2 refracCoords = projCoord.xy + dudvCoord.rb * kRefraction;
	refracCoords = clamp(refracCoords, kRefraction, 1-kRefraction);
	
	vec3 refraction;
	
	// under water only refraction, no reflection 
	if (isCameraUnderWater == 1){
		reflection = vec3(0,0,0);
		refraction = texture(waterRefraction, refracCoords).rgb;
	}
	else {
		refraction = texture(waterRefraction, refracCoords).rgb;
		refraction *= 1-fresnel;
	}
	
	vec3 fragColor = reflection + refraction;
	
	float spec = specularReflection(vec3(directional_light.direction.x, directional_light.direction.y/specularAmplifier, directional_light.direction.z),
		normal.xzy, specularFactor, emission, vertexToEye);
	vec3 specularLight = directional_light.color * spec;
	
	// fragColor += specularLight;
	
	if (diffuseEnable == 0)
		normal = vec3(0,0,1);
	
	albedo_out = vec4(fragColor,1);
	worldPosition_out = vec4(inPosition,1);
	normal_out = vec4(normal,1);
	specular_emission_diffuse_ssao_bloom_out = vec4(280,2,0,1);
	lightScattering_out = vec4(0,0,0,1);
}
