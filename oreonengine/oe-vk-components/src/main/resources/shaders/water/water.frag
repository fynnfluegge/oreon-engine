#version 430
#define M_PI 3.1415926535897932384626433832795

layout (location = 0) in vec3 inPosition;
layout (location = 1) in vec2 inUV;
layout (location = 2) in vec3 inTangent;

layout(location = 0) out vec4 albedo_out;
// layout(location = 1) out vec4 worldPosition_out;
layout(location = 1) out vec4 normal_out;
// layout(location = 3) out vec4 specularEmission_out;
// layout(location = 4) out vec4 lightScattering_out;

layout(set = 0, binding = 0, std140, row_major) uniform Camera {
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

// layout (binding = 1, set = 0, std140) uniform DirectionalLight{
	// vec3 direction;
	// float intensity;
	// vec3 ambient;
	// vec3 color;
// } directional_light;

layout(set = 1, binding = 3) uniform sampler2D dudvRefracReflec;
layout(set = 1, binding = 4) uniform sampler2D normalmap;
layout(set = 1, binding = 5) uniform sampler2D waterReflection;
layout(set = 1, binding = 6) uniform sampler2D waterRefraction;

layout(set = 1, binding = 7) uniform UBO {
	float motion;
	float distortion;
} ubo;

layout (push_constant, std430, row_major) uniform Constants{
	mat4 m_World;
	vec2 windDirection;
	float tessSlope;
	float tessShift;
	int tessFactor;
	int uvScale;
	float displacementScale;
	float choppiness;
	int highDetailRange;
	float kReflection;
	float kRefraction;
	int windowWidth;
	int windowHeight;
	float emission;
	float specular;
} constants;

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

float specularReflection(vec3 direction, vec3 normal, vec3 eyePosition, vec3 vertexPosition,
	float specularFactor, float emissionFactor)
{
	normal.xz *= 2.2;

	vec3 reflectionVector = normalize(reflect(direction, normalize(normal)));
	vec3 vertexToEye = normalize(eyePosition - vertexPosition);
	
	float specular = max(0.0, dot(vertexToEye, reflectionVector));
	
	return pow(specular, specularFactor) * emissionFactor;
}
 
void main(void)
{
	vertexToEye = normalize(eyePosition - inPosition);
	float dist = length(eyePosition - inPosition);
	
	// normal
	vec3 normal = normalize(texture(normalmap, inUV + (constants.windDirection*ubo.motion)).rgb);
	
	if (dist < constants.highDetailRange-50){
		
		float attenuation = clamp(-dist/(constants.highDetailRange-50) + 1,0.0,1.0);
		
		vec3 bitangent = normalize(cross(inTangent, normal));
		mat3 TBN = mat3(inTangent,bitangent,normal);
		vec3 bumpNormal = normalize(texture(normalmap, inUV*8).rgb);
		bumpNormal.z *= 2.8;
		bumpNormal.xy *= attenuation;
		
		bumpNormal = normalize(bumpNormal);
		
		normal = normalize(TBN * bumpNormal);
	}
	
	float fresnel = fresnelApproximated(normal.xzy);
	
	// projCoord //
	vec3 dudvCoord = normalize((2 * texture(dudvRefracReflec, inUV*4 + ubo.distortion).rbg) - 1);
	vec2 projCoord = vec2(gl_FragCoord.x/constants.windowWidth, gl_FragCoord.y/constants.windowHeight);
 
    // Reflection //
	vec2 reflecCoords = projCoord.xy + dudvCoord.rb * constants.kReflection;
	reflecCoords = clamp(reflecCoords, constants.kReflection, 1-constants.kReflection);
    vec3 reflection = mix(texture(waterReflection, reflecCoords).rgb, deepOceanColor,  0.5);
    reflection *= fresnel;
 
    // Refraction //
	vec2 refracCoords = projCoord.xy + dudvCoord.rb * constants.kRefraction;
	refracCoords = clamp(refracCoords, constants.kRefraction, 1-constants.kRefraction);
	vec3 refraction = texture(waterRefraction, refracCoords).rgb;
	refraction *= 1-fresnel;
	
	vec3 fragColor = (reflection + refraction);
	
	float spec = specularReflection(vec3(0,-1,0), normal.xzy, eyePosition, inPosition, constants.specular, constants.emission);
	vec3 specularLight = (vec3(1,1,1)) * spec;
	// float spec = specularReflection(directional_light.direction, normal.xzy, eyePosition, position_FS, specular, emission);
	// vec3 specularLight = (directional_light.color + vec3(0,0.03,0.08)) * spec;
	
	fragColor += specularLight;
	
	albedo_out = vec4(fragColor,1);
	// worldPosition_out = vec4(inPosition,1);
	normal_out = vec4(normal,1);
	// specularEmission_out = vec4(1,0,0,1);
	// lightScattering_out = vec4(0,0,0,1);
}
