#version 430

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba32f) uniform writeonly image2D ssao_out;

layout (binding = 1, rgba32f) uniform readonly image2DMS worldPositionImage;

layout (binding = 2, rgba32f) uniform readonly image2DMS normalImage;

layout (binding = 3, rgba32f) uniform readonly image2D noiseImage;

layout (binding = 4, rgba32f) uniform readonly image2D depthImage;

layout (binding = 5, rgba32f) uniform readonly image2DMS depthImageMs;

uniform vec3 kernel[32];
uniform mat4 m_View;
uniform mat4 m_Proj;
uniform int kernelSize;
uniform float uRadius;

const float zfar = 10000.0f;
const float znear = 0.1f;
const float rangeCheckThreshold = 0.01f;

float linearizeDepth(float depth)
{
	return (2 * znear) / (zfar + znear - depth * (zfar - znear));
}

void main(void){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.xy);
	
	float actualDepth = linearizeDepth(imageLoad(depthImageMs, computeCoord, 0).r);
	
	vec3 worldPosition = imageLoad(worldPositionImage, computeCoord, 0).rgb;
	
	vec3 normal = imageLoad(normalImage, computeCoord, 0).rgb;
	
	vec3 viewPosition = (m_View * vec4(worldPosition,1.0)).xyz;
	
	float viewSpaceDepth = viewPosition.z;
	
	ivec2 noiseCoord = computeCoord - ivec2(floor(gl_GlobalInvocationID.x/4), floor(gl_GlobalInvocationID.y/4)) * 4; 
	
	vec3 rvec = imageLoad(noiseImage, noiseCoord).rgb;
	vec3 tangent = normalize(rvec - normal * dot(rvec, normal));
	vec3 bitangent = cross(normal, tangent);
	mat3 tbn = mat3(tangent, bitangent, normal);
	
	float occlusion = 0.0;
	for (int i = 0; i < kernelSize; ++i) {
		// get sample position:
		vec3 smple = tbn * kernel[i];
		smple = smple * uRadius + viewPosition;
	  
		// project sample position:
		vec4 offset = vec4(smple, 1.0);
		offset = m_Proj * offset;
		offset.xy /= offset.w;
		offset.xy = offset.xy * 0.5 + 0.5;
	  
		// get sample depth:
		float sampleDepth = (m_View * vec4(imageLoad(worldPositionImage, ivec2(offset.x * 1280, offset.y * 720), 0).rgb,1.0)).z;
	  
		// range check & accumulate:
		float rangeCheck= abs(viewSpaceDepth - sampleDepth) < rangeCheckThreshold ? 1.0 : 0.0;
		occlusion += (sampleDepth <= smple.z ? 1.0 : 0.0);// * rangeCheck;
	}
	
	occlusion = (1.0 - (occlusion / float(kernelSize)));
	
	occlusion = pow(occlusion,2) * 2;
	
	imageStore(ssao_out, computeCoord, vec4(occlusion,occlusion,occlusion,1.0));
}