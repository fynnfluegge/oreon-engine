#version 430

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba32f) uniform writeonly image2D ssao_out;

layout (binding = 1, rgba32f) uniform readonly image2DMS worldPositionImage;

layout (binding = 2, rgba32f) uniform readonly image2DMS normalImage;

layout (binding = 3, rgba32f) uniform readonly image2D noiseImage;

uniform vec3 kernel[64];
uniform mat4 m_View;
uniform mat4 m_Proj;
uniform int kernelSize;
uniform float uRadius;
uniform float threshold;
uniform int width;
uniform int height;

const float zfar = 10000.0f;

void main(void){

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.xy);
	
	vec3 worldPosition = imageLoad(worldPositionImage, computeCoord, 0).rgb;
	
	vec3 viewPosition = (m_View * vec4(worldPosition,1.0)).xyz;
	
	float actualDepth = viewPosition.z/zfar;
	
	vec3 normal = imageLoad(normalImage, computeCoord, 0).rgb;
	
	ivec2 noiseCoord = computeCoord - ivec2(floor(gl_GlobalInvocationID.x/4), floor(gl_GlobalInvocationID.y/4)) * 4; 
	
	vec3 rvec = imageLoad(noiseImage, noiseCoord).rgb;
	vec3 tangent = normalize(rvec - normal * dot(rvec, normal));
	vec3 bitangent = cross(normal, tangent);
	mat3 tbn = mat3(tangent, bitangent, normal);
	
	float occlusion = 0.0;
	float occlusionOffset = 0.0;
	for (int i = 0; i < kernelSize; ++i) {
		// get sample position:
		vec3 smple = tbn * kernel[i];
		smple = smple * uRadius + viewPosition;
	  
		// project sample position:
		vec4 offset = vec4(smple, 1.0);
		offset = m_Proj * offset;
		offset.xy /= offset.w;
		offset.xy = offset.xy * 0.5 + 0.5;
		  
		if (offset.x < 1.0 && offset.y < 1.0 && offset.x >= 0.0 && offset.y >= 0.0){
			// get sample depth:
			float sampleDepth = (m_View * vec4(imageLoad(worldPositionImage, ivec2(offset.x * width, offset.y * height), 0).rgb,1.0)).z;
		  
			// range check & accumulate:
			float rangeCheck= abs(actualDepth - sampleDepth) < threshold ? 1.0 : 0.0;
			
			occlusionOffset = (sampleDepth/zfar <= smple.z/zfar ? 1.0 : 0.0) * rangeCheck;
			occlusion += occlusionOffset;
		}
		else{
			occlusion += 0.4f;
		}
	}
	
	occlusion = (1.0 - (occlusion / float(kernelSize)));
	
	imageStore(ssao_out, computeCoord, vec4(occlusion,occlusion,occlusion,1.0));
}