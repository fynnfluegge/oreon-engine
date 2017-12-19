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

layout (std140) uniform DirectionalLight{
	vec3 direction;
	float intensity;
	vec3 ambient;
	vec3 color;
} directional_light;

uniform int largeDetailRange;
uniform mat4 modelViewProjectionMatrix;
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
const vec3 deepOceanColor = vec3(0.1,0.125,0.20);
const float zfar = 10000;
const float znear = 0.1;
vec3 vertexToEye;

float fresnelApproximated(vec3 normal)
{
	vec3 halfDirection = normalize(normal + vertexToEye);
    
    float cosine = dot(halfDirection, vertexToEye);

	float fresnel = Eta + (1.0 - Eta) * pow(max(0.0, 1.0 - dot(vertexToEye, normal)), 6.0);
	
	return clamp(pow(fresnel, 1.0),0.0,1.0);
}

float specularReflection(vec3 direction, vec3 normal, vec3 eyePosition, vec3 vertexPosition, float specularFactor, float emissionFactor)
{
	normal.xz *= 2.2;

	vec3 reflectionVector = normalize(reflect(direction, normalize(normal)));
	vec3 vertexToEye = normalize(eyePosition - vertexPosition);
	
	float specular = max(0.0, dot(vertexToEye, reflectionVector));
	
	return pow(specular, specularFactor) * emissionFactor;
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
    vec3 reflection = mix(texture(waterReflection, reflecCoords).rgb, deepOceanColor,  0.5);
    reflection *= F;
 
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
	
	float spec = specularReflection(directional_light.direction, normal.xzy, eyePosition, position_FS, specular, emission);
	vec3 specularLight = (directional_light.color + vec3(0,0.03,0.08)) * spec;
	
	fragColor += specularLight;
	
	albedo_out = vec4(fragColor,1);
	worldPosition_out = vec4(position_FS,1);
	normal_out = vec4(normal,1);
	specularEmission_out = vec4(1,0,0,1);
	lightScattering_out = vec4(0,0,0,1);
}
