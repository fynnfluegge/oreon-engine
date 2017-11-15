#version 430
#define M_PI 3.1415926535897932384626433832795

in vec3 position_FS;
in vec2 texCoord_FS;
in vec3 tangent;

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 worldPosition_out;
layout(location = 2) out vec4 normal_out;
layout(location = 3) out vec4 specularEmission_out;
layout(location = 4) out vec4 lightScattering_out;

struct DirectionalLight
{
	float intensity;
	vec3 ambient;
	vec3 direction;
	vec3 color;
};

uniform int largeDetailRange;
uniform mat4 modelViewProjectionMatrix;
uniform DirectionalLight sunlight;
uniform sampler2D waterReflection;
uniform sampler2D waterRefraction;
uniform sampler2D dudvRefracReflec;
uniform float distortionRefracReflec;
uniform sampler2D dudvCaustics;
uniform float distortionCaustics;
uniform sampler2D caustics;
uniform float motion;
uniform sampler2D normalmap;
uniform vec3 eyePosition;
uniform float kReflection;
uniform float kRefraction;
uniform int windowWidth;
uniform int windowHeight;
uniform int texDetail;
uniform float emission;
uniform float specular;
uniform float sightRangeFactor;
uniform int isCameraUnderWater;

const vec2 wind = vec2(1,1);
const float Eta = 0.15; // Water
const vec3 deepOceanColor = vec3(0.1,0.125,0.19);
const vec3 fogColor = vec3(0.62,0.8,0.98);
const float zfar = 10000;
const float znear = 0.1;
vec3 vertexToEye;

float fresnelApproximated(vec3 normal)
{
	vec3 halfDirection = normalize(normal + vertexToEye);
    
    float cosine = dot(halfDirection, vertexToEye);

	float fresnel = Eta + (1.0 - Eta) * pow(max(0.0, 1.0 - dot(vertexToEye, normal)), 2.0);
	
	return pow(fresnel,0.8);
}
 
void main(void)
{
	vertexToEye = normalize(eyePosition - position_FS);
	float dist = length(eyePosition - position_FS);
	
	// normal
	vec3 normal = texture(normalmap, texCoord_FS + (wind*motion)).rgb;

	normal = normalize(normal);
	
	if (dist < largeDetailRange-50){
		
		float attenuation = clamp(-dist/(largeDetailRange-50) + 1,0.0,1.0);
		
		vec3 bitangent = normalize(cross(tangent, normal));
		mat3 TBN = mat3(tangent,bitangent,normal);
		vec3 bumpNormal = texture(normalmap, texCoord_FS*8).rgb;
		bumpNormal.z *= 2.8;
		bumpNormal.xy *= attenuation;
		
		bumpNormal = normalize(bumpNormal);
		
		normal = normalize(TBN * bumpNormal);
	}
	
	float F = fresnelApproximated(normal.xzy);
	
	// projCoord //
	vec3 dudvCoord = normalize((2 * texture(dudvRefracReflec, texCoord_FS*4 + distortionRefracReflec).rbg) - 1);
	vec2 projCoord = vec2(gl_FragCoord.x/windowWidth, gl_FragCoord.y/windowHeight);
 
    // Reflection //
	vec2 reflecCoords = projCoord.xy + dudvCoord.rb * kReflection;
	reflecCoords = clamp(reflecCoords, kReflection, 1-kReflection);
    vec3 reflection = mix(texture(waterReflection, reflecCoords).rgb, deepOceanColor,  0.4);
    reflection *= F;
 
    // Refraction //
	vec2 refracCoords = projCoord.xy + dudvCoord.rb * kRefraction;
	refracCoords = clamp(refracCoords, kRefraction, 1-kRefraction);
	
	vec3 refraction = vec3(0,0,0);
	
	// under water only refraction, no reflection 
	if (isCameraUnderWater == 1){
		reflection = vec3(0,0,0);
		refraction = texture(waterRefraction, refracCoords).rgb;
	}
	else {
		refraction = mix(texture(waterRefraction, refracCoords).rgb, deepOceanColor, 0.1);
		refraction *= 1-F;
	}
	
	vec3 fragColor = (reflection + refraction);
	
	// caustics
	if (isCameraUnderWater == 1){
		vec2 causticsTexCoord = position_FS.xz / 80;
		vec2 causticDistortion = texture(dudvCaustics, causticsTexCoord*0.2 + distortionCaustics*0.6).rb * 0.18;
		vec3 causticsColor = texture(caustics, causticsTexCoord + causticDistortion).rbg;
		
		fragColor += (causticsColor/4);
	}
	
	float fogFactor = -0.0002/sightRangeFactor*(dist-(zfar)/10*sightRangeFactor) + 1;
	
    vec3 rgb = mix(fogColor, fragColor, clamp(fogFactor,0,1));
	
	albedo_out = vec4(rgb,1);
	worldPosition_out = vec4(position_FS,1);
	normal_out = vec4(normal,1);
	specularEmission_out = vec4(specular,emission,0,1);
	lightScattering_out = vec4(0,0,0,1);
}
