#version 430

in vec2 texCoordF;
in vec3 positionF;
flat in int highDetailF;
flat in vec3 tangent;


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
	sampler2D displacemap;
	float displaceScale;
	float shininess;
	float emission;
};

const int maxLights = 10;


uniform sampler2D normalmap;
uniform sampler2D occMap;
uniform sampler2D splatmap;
uniform int numLights;
uniform PointLight lights[maxLights];
uniform vec3 eyePosition;
uniform DirectionalLight sunlight;
uniform float scaleY;
uniform float scaleXZ;
uniform Material sand;
uniform Material rock;
uniform Material snow;
uniform float sightRangeFactor;


const float zFar = 10000;
const vec3 fogColor = vec3(0.8,0.8,0.8);

float emission;
float shininess;

float diffuse(vec3 direction, vec3 normal, float intensity)
{
	return max(0.05, dot(normal, -direction) * intensity);
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
	float sightRange = zFar/5*sightRangeFactor+400;
	// if (dist > sightRange) discard;
	
	// normalmap/occlusionmap/splatmap coords
	vec2 mapCoords = vec2((positionF.x + scaleXZ/2)/scaleXZ, (positionF.z + scaleXZ/2)/scaleXZ);
	
	float height = positionF.y/scaleY;
	float sandBlending = texture(splatmap, mapCoords).b;
	float rockBlending = texture(splatmap, mapCoords).g;
	float snowBlending = texture(splatmap, mapCoords).r;
	

	vec3 normal = normalize((2*(texture(normalmap, vec2(mapCoords.x, mapCoords.y)).rbg)-1));
	
	if (highDetailF ==  1)
	{
		vec3 Bitangent = normalize(cross(tangent, normal));
		mat3 TBN = mat3(tangent,normal,Bitangent);
		
		vec3 sandNRM = normalize(2*(texture(sand.normalmap, texCoordF).rbg)-1);
		vec3 rockNRM = normalize(2*(texture(rock.normalmap, texCoordF).rbg)-1);
		vec3 snowNRM = normalize(2*(texture(snow.normalmap, texCoordF).rbg)-1);
		
		vec3 bumpNormal = normalize(sandBlending * sandNRM + rockBlending * rockNRM + snowBlending * snowNRM);
		
		normal = normalize(TBN * bumpNormal);
	}
	
	vec3 diffuseLight = vec3(0.0);
	vec3 specularLight = vec3(0.0);
	float attenuation = 1.0;
	float diffuseFactor = 0.0;
	float specularFactor = 0.0;
	
	emission = sandBlending * sand.emission + rockBlending * rock.emission + snowBlending * snow.emission;
	shininess =sandBlending * sand.shininess + rockBlending * rock.shininess + snowBlending * snow.shininess;
	
	
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
	
	diffuseFactor = diffuse(sunlight.direction, normal, sunlight.intensity);
	specularFactor = specular(sunlight.direction, normal, eyePosition, positionF);
	
	float ambientOcclusion = texture(occMap, mapCoords).r;
	
	vec3 sandTexel = texture(sand.diffusemap, texCoordF).xyz;
	vec3 rockTexel = texture(rock.diffusemap, texCoordF).xyz;
	vec3 snowTexel = texture(snow.diffusemap, texCoordF).xyz;
	
	diffuseLight  += vec3(sunlight.color) * diffuseFactor;
	specularLight += vec3(sunlight.color) * specularFactor;
	
	vec3 fragColor = (min(vec3(1.0),sandTexel * sandBlending + rockTexel * rockBlending + snowTexel * snowBlending)  * diffuseLight + specularLight) * sunlight.ambient * ambientOcclusion;
	
	float fogFactor = -0.0005/sightRangeFactor*(dist-zFar/5*sightRangeFactor - 400*sightRangeFactor);
    fogFactor = clamp(fogFactor, 0.0, 1.0 );
	
    vec3 rgb = mix(fogColor, fragColor, fogFactor);
	
	float alpha = 1;
	if (fogFactor < 0.5)
		alpha = -0.001/sightRangeFactor * (dist-zFar/5*sightRangeFactor - 400*sightRangeFactor);
	
	gl_FragColor = vec4(rgb,1.0);
}