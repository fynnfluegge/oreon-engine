const int shadowMapResolution = 4096;
const float zfar = 10000;
const float znear = 0.1;
const vec3 sunBaseColor = vec3(1.0f,0.79f,0.43f);

float linearizeDepth(float depth)
{
	return (2 * znear) / (zfar + znear - depth * (zfar - znear));
}

float specular(vec3 direction, vec3 normal, float specularFactor, float emissionFactor, vec3 vertexToEye)
{
	vec3 reflectionVector = normalize(reflect(direction, normalize(normal)));
	
	float specular = max(0.0, dot(vertexToEye, reflectionVector));
	
	return pow(specular, specularFactor) * emissionFactor;
}

float specular(vec3 direction, vec3 normal, vec3 eyePosition, vec3 vertexPosition, float specularFactor, float emissionFactor)
{
	vec3 reflectionVector = normalize(reflect(direction, normal));
	vec3 vertexToEye = normalize(eyePosition - vertexPosition);
	
	float specular = max(0.0, dot(vertexToEye, reflectionVector));
	
	return pow(specular, specularFactor) * emissionFactor;
}

float diffuse(vec3 direction, vec3 normal, float intensity)
{
	return max(0.0, dot(normal, -direction) * intensity);
}

float getFogFactor(float dist)
{
	return smoothstep(0,1,-0.0002/sightRangeFactor*(dist-(zfar)/10*sightRangeFactor) + 1);
}

float distancePointPlane(vec3 point, vec4 plane){
	return abs(plane.x*point.x + plane.y*point.y + plane.z*point.z + plane.w) / 
		   abs(sqrt(plane.x * plane.x + plane.y * plane.y + plane.z * plane.z));
}

float getLodfactor(float distance){
	
	float tessLevel = max(0.0,tessFactor/(pow(distance, tessSlope)) - tessShift);

	return tessLevel;
}

vec3 getTangent(vec3 v0, vec3 v1, vec3 v2)
{	
	// edges of the face/triangle
    vec3 e1 = v1 - v0;
    vec3 e2 = v2 - v0;
	
	vec2 uv0 = inUV[0];
	vec2 uv1 = inUV[1];
	vec2 uv2 = inUV[2];

    vec2 deltaUV1 = uv1 - uv0;
	vec2 deltaUV2 = uv2 - uv0;
	
	float r = 1.0 / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);
	
	return normalize((e1 * deltaUV2.y - e2 * deltaUV1.y)*r);
}

float percentageCloserShadows(vec3 projCoords, int split, float shadowFactor)
{
	float currentDepth = projCoords.z;
	float shadowMapDepth = texture(pssm, vec3(projCoords.xy,split)).r;
	
	float dist = linearizeDepth(shadowMapDepth) - linearizeDepth(currentDepth);
		
	if (dist < 0)
		return 0;
	else 
		return 1;
}

float varianceShadow(vec3 projCoords, int split, int kernels)
{	
	float shadowFactor = 1.0;
	float texelSize = 1.0/ 4096.0;
	float currentDepth = projCoords.z;
	float reduceFactor = 1/ pow(kernels*2+1,2);
	
	for (int i=-kernels; i<=kernels; i++){
		for (int j=-kernels; j<=kernels; j++){
			float shadowMapDepth = texture(pssm, vec3(projCoords.xy,split)
													   + vec3(i,j,0) * texelSize).r; 
			if (currentDepth > shadowMapDepth)
				shadowFactor -= reduceFactor;
		}
	}
	
	return max(0.1,shadowFactor);
}

float applyShadowMapping(vec3 worldPos)
{
	float shadowFactor = 0;
	vec3 projCoords = vec3(0,0,0);
	float linDepth = (m_View * vec4(worldPos,1)).z/zfar;
	if (linDepth < splitRange[0]){
		vec4 lightSpacePos = m_lightViewProjection[0] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor0 = varianceShadow(projCoords,0,4);
		
		lightSpacePos = m_lightViewProjection[1] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor1 = varianceShadow(projCoords,1,4);
		
		lightSpacePos = m_lightViewProjection[2] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor2 = varianceShadow(projCoords,2,2);
		
		shadowFactor = min(shadowFactor2, min(shadowFactor0,shadowFactor1));
	}
	else if (linDepth < splitRange[1]){
		vec4 lightSpacePos = m_lightViewProjection[1] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor0 = varianceShadow(projCoords,1,4);
		
		lightSpacePos = m_lightViewProjection[2] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		float shadowFactor1 = varianceShadow(projCoords,2,2);
		
		shadowFactor = min(shadowFactor0,shadowFactor1);
	}
	else if (linDepth < splitRange[2]){
		vec4 lightSpacePos = m_lightViewProjection[2] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,2,2);
	}
	else if (linDepth < splitRange[3]){
		vec4 lightSpacePos = m_lightViewProjection[3] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,3,2);
	}
	else if (linDepth < splitRange[4]){
		vec4 lightSpacePos = m_lightViewProjection[4] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,4,1); 
	}
	else if (linDepth < splitRange[5]){
		vec4 lightSpacePos = m_lightViewProjection[5] * vec4(worldPos,1.0);
		projCoords = lightSpacePos.xyz * 0.5 + 0.5;
		shadowFactor = varianceShadow(projCoords,5,1); 
	}
	else return 1;
	
	return shadowFactor;
}