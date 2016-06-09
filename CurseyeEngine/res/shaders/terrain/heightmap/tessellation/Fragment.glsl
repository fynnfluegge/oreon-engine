#version 430

in vec2 texCoordF;
in vec3 positionF;
in vec3 tangent;

struct DirectionalLight
{
	float intensity;
	vec3 ambient;
	vec3 direction;
	vec3 color;
};

struct PointLight
{
	int isEnabled;
	int isSpot;
	vec3 position;
	vec3 color;
	vec3 ambient;
	float intensity;
	float ConstantAttenuation;
	float LinearAttenuation;
	float QuadraticAttenuation;
	vec3 ConeDirection;
	float SpotCosCutoff;
	float SpotExponent;
};

struct Material
{
	sampler2D diffusemap;
	sampler2D normalmap;
	float shininess;
	float emission;
};

struct Fractal
{
	sampler2D normalmap;
	int scaling;
};

const int maxLights = 10;
uniform Fractal fractals1[10];
uniform sampler2D normalmap;
uniform sampler2D occMap;
uniform sampler2D splatmap;
uniform int numLights;
uniform PointLight lights[maxLights];
uniform vec3 eyePosition;
uniform DirectionalLight sunlight;
uniform float scaleY;
uniform float scaleXZ;
uniform Material sand1;
uniform Material rock1;
uniform Material snow1;
uniform float sightRangeFactor;
uniform int largeDetailedRange;

const float zFar = 10000;
const vec3 fogColor = vec3(0.62,0.85,0.95);

float emission;
float shininess;

float diffuse(vec3 direction, vec3 normal, float intensity)
{
	return max(0.0, dot(normal, -direction) * intensity);
}

float specular(vec3 direction, vec3 normal, vec3 eyePosition, vec3 vertexPosition)
{
	vec3 reflectionVector = normalize(reflect(direction, normal));
	vec3 vertexToEye = normalize(eyePosition - vertexPosition);
	
	float specular = max(0, dot(vertexToEye, reflectionVector));
	
	return pow(specular, shininess) * emission;
}

void main()
{		
	float dist = length(eyePosition - positionF);
	
	// normalmap/occlusionmap/splatmap coords
	vec2 mapCoords = vec2((positionF.x + scaleXZ/2)/scaleXZ, (positionF.z + scaleXZ/2)/scaleXZ);
	
	float sandBlending = texture(splatmap, mapCoords).b;
	float rockBlending = texture(splatmap, mapCoords).g;
	float snowBlending = texture(splatmap, mapCoords).r;
	

	 vec3 normal = normalize( (2*(texture(normalmap,mapCoords).rbg)-1)*0
							+(2*(texture(fractals1[0].normalmap, mapCoords*fractals1[0].scaling).rbg)-1)
							+(2*(texture(fractals1[1].normalmap, mapCoords*fractals1[1].scaling).rbg)-1)
							+(2*(texture(fractals1[2].normalmap, mapCoords*fractals1[2].scaling).rbg)-1)
							+(2*(texture(fractals1[3].normalmap, mapCoords*fractals1[3].scaling).rbg)-1)
							+(2*(texture(fractals1[4].normalmap, mapCoords*fractals1[4].scaling).rbg)-1)
							+(2*(texture(fractals1[5].normalmap, mapCoords*fractals1[5].scaling).rbg)-1)
							+(2*(texture(fractals1[6].normalmap, mapCoords*fractals1[6].scaling).rbg)-1)
							);
	
	if (dist < largeDetailedRange-20)
	{
		float attenuation = -dist/(largeDetailedRange-20) + 1;
		
		vec3 bitangent = normalize(cross(tangent, normal));
		mat3 TBN = mat3(tangent,normal,bitangent);
		
		vec3 sandNRM = normalize(2*(texture(sand1.normalmap, texCoordF).rbg)-1);
		vec3 rockNRM = normalize(2*(texture(rock1.normalmap, texCoordF).rbg)-1);
		vec3 snowNRM = normalize(2*(texture(snow1.normalmap, texCoordF).rbg)-1);
		
		vec3 bumpNormal = normalize(sandBlending * sandNRM + rockBlending * rockNRM + snowBlending * snowNRM);
		
		bumpNormal.xz *= attenuation;
		
		normal = normalize(TBN * bumpNormal);
	}
	
	vec3 diffuseLight = vec3(0.0);
	vec3 specularLight = vec3(0.0);
	float attenuation = 1.0;
	float diffuseFactor = 0.0;
	float specularFactor = 0.0;
	
	emission = sandBlending * sand1.emission + rockBlending * rock1.emission + snowBlending * snow1.emission;
	shininess =sandBlending * sand1.shininess + rockBlending * rock1.shininess + snowBlending * snow1.shininess;
	
	
	for (int light = 0; light < numLights; ++light) {
		if (lights[light].isEnabled == 0)
			continue;
		vec3 lightDirection =  positionF - lights[light].position;
		float lightDistance = length(lightDirection);
		lightDirection = normalize(lightDirection);
		attenuation = 1.0 /
			(lights[light].ConstantAttenuation
			+ lights[light].LinearAttenuation * lightDistance
			+ lights[light].QuadraticAttenuation * lightDistance * lightDistance);
		if (lights[light].isSpot == 1) {
			float spotCos = dot(lightDirection, lights[light].ConeDirection);
			if (spotCos < lights[light].SpotCosCutoff)
				attenuation = 0.0;
			else
				attenuation *= pow(spotCos, lights[light].SpotExponent);
		}
		
		diffuseFactor = diffuse(lightDirection, normal, lights[light].intensity);
		if (diffuseFactor == 0.0)
			specularFactor = 0.0;
		else
			specularFactor = specular(lightDirection, normal, eyePosition, positionF);
		
		diffuseLight  += lights[light].color * diffuseFactor * attenuation * lights[light].ambient;
		specularLight += lights[light].color * specularFactor * attenuation;
	}
	
	float ambientOcclusion = texture(occMap, mapCoords).r;
	
	vec3 sandTexel = texture(sand1.diffusemap,texCoordF).xyz;
	vec3 rockTexel = texture(rock1.diffusemap,texCoordF).xyz;
	vec3 snowTexel = texture(snow1.diffusemap,texCoordF).xyz;
	
	float diffuse = diffuse(sunlight.direction, normal, sunlight.intensity);
	float specular = specular(sunlight.direction, normal, eyePosition, positionF);
	diffuseLight = sunlight.ambient + sunlight.color * diffuse;
	specularLight = sunlight.color * specular;
	
	vec3 fragColor = (sandTexel * sandBlending + rockTexel * rockBlending + snowTexel * snowBlending)  * diffuseLight + specularLight;
	
	float fogFactor = -0.0005/sightRangeFactor*(dist-zFar/5*sightRangeFactor);
	
    vec3 rgb = mix(fogColor, fragColor, clamp(fogFactor,0,1));
	
	gl_FragColor = vec4(rgb,1);
}