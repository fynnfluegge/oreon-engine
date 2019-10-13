#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec3 inPosition;
layout (location = 1) in vec2 inUV;
layout (location = 2) in vec3 inTangent;

layout(location = 0) out vec4 albedo_out;
layout(location = 1) out vec4 worldPosition_out;
layout(location = 2) out vec4 normal_out;
layout(location = 3) out vec4 specular_emission_diffuse_ssao_bloom_out;
layout(location = 4) out vec4 lightScattering_out;

layout(set = 0, binding = 0, std140, row_major) uniform Camera {
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

layout(set = 1, binding = 0) uniform sampler2D Dy;
layout(set = 1, binding = 1) uniform sampler2D Dx;
layout(set = 1, binding = 2) uniform sampler2D Dz;
layout(set = 1, binding = 3) uniform sampler2D dudvRefracReflec;
layout(set = 1, binding = 4) uniform sampler2D normalmap;
layout(set = 1, binding = 5) uniform sampler2D waterReflection;
layout(set = 1, binding = 6) uniform sampler2D waterRefraction;

layout(set = 1, binding = 7) uniform UBO {
	float motion;
	float distortion;
} ubo;

layout (push_constant, std430, row_major) uniform Constants{
	mat4 worldMatrix;
	int uvScale;
	int tessFactor;
	float tessSlope;
	float tessShift;
	float displacementScale;
	int highDetailRange;
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
	vec2 windDirection;
} constants;

const float Eta = 0.15; // Water
const float zfar = 10000;
const float znear = 0.1;

float fresnelApproximated(vec3 normal, vec3 vertexToEye)
{
	vec3 halfDirection = normalize(normal + vertexToEye);
    
    float cosine = dot(halfDirection, vertexToEye);

	float fresnel = Eta + (1.0 - Eta) * pow(max(0.0, 1.0 - dot(vertexToEye, normal)), constants.fresnelFactor);
	
	return clamp(pow(fresnel, 1.0),0.0,1.0);
}
 
void main(void)
{
	vec3 vertexToEye = normalize(eyePosition - inPosition);
	float dist = length(eyePosition - inPosition);
	
	// normal
	vec3 normal = (texture(normalmap, inUV + (constants.windDirection*ubo.motion)).rgb);
	normal = normalize(normal);
	
	if (dist < constants.highDetailRange-50){
		
		float attenuation = clamp(-dist/(constants.highDetailRange-50) + 1,0.0,1.0);
		
		vec3 bitangent = normalize(cross(inTangent, normal));
		mat3 TBN = mat3(inTangent,bitangent,normal);
		vec3 bumpNormal = normalize(texture(normalmap, inUV*constants.capillarDownsampling).rgb);
		bumpNormal.z *= constants.capillarStrength;
		bumpNormal.xy *= attenuation;
		
		bumpNormal = normalize(bumpNormal);
		
		normal = normalize(TBN * bumpNormal);
	}
	
	float fresnel = fresnelApproximated(normal.xzy,vertexToEye);
	
	// projCoord //
	vec3 dudvCoord = normalize((2 * texture(dudvRefracReflec, inUV*constants.dudvDownsampling + ubo.distortion).rbg) - 1);
	vec2 projCoord = vec2(gl_FragCoord.x/constants.windowWidth, gl_FragCoord.y/constants.windowHeight);
 
    // Reflection //
	vec2 reflecCoords = projCoord.xy + dudvCoord.rb * constants.kReflection;
	reflecCoords = clamp(reflecCoords, constants.kReflection, 1-constants.kReflection);
    float reflectionDistanceBlending = smoothstep(0.0f,1.0f,1.0f/2000.0f * dist) * (0.75f-constants.reflectionBlendFactor) + constants.reflectionBlendFactor;
    vec3 reflection = mix(texture(waterReflection, reflecCoords).rgb, constants.waterColor, reflectionDistanceBlending);
    reflection *= fresnel;
 
    // Refraction //
	vec2 refracCoords = projCoord.xy + dudvCoord.rb * constants.kRefraction;
	refracCoords = clamp(refracCoords, constants.kRefraction, 1-constants.kRefraction);
	vec3 refraction = texture(waterRefraction, refracCoords).rgb;
	refraction *= 1-fresnel;
	
	vec3 fragColor = (reflection + refraction);
	
	float diffuseSsao = 0;
	if (constants.diffuseEnable == 1)
		diffuseSsao = 10;
	
	albedo_out = vec4(fragColor,1);
	worldPosition_out = vec4(inPosition,gl_FragCoord.z);
	normal_out = vec4(normal,1);
	specular_emission_diffuse_ssao_bloom_out = vec4(constants.specularFactor,constants.emission,diffuseSsao,1);
	lightScattering_out = vec4(0,0,0,1);
}
