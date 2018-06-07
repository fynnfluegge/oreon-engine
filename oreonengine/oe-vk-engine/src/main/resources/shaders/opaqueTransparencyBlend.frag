#version 450
#extension GL_ARB_separate_shader_objects : enable

layout (location = 0) in vec2 inUV;

layout(location = 0) out vec4 fragColor_out;
layout(location = 1) out vec4 lightScattering_out;

layout (set = 0, binding = 0) uniform sampler2D opaqueSceneSampler;
layout (set = 0, binding = 1) uniform sampler2D opaqueSceneLightScatteringSampler;
layout (set = 0, binding = 2) uniform sampler2DMS opaqueSceneDepthMap;
layout (set = 0, binding = 3) uniform sampler2D transparencySampler;
layout (set = 0, binding = 4) uniform sampler2D transparencyLayerLightScatteringSampler;
layout (set = 0, binding = 5) uniform sampler2D transparencyLayerDepthMap;
layout (set = 0, binding = 6) uniform sampler2D transparencyAlphaMap;

layout (push_constant, std430, row_major) uniform pushConstants{
	float width;
	float height;
} constants;

const float zfar = 10000;
const float znear = 0.1;

float linearizeDepth(float depth)
{
	return (2 * znear) / (zfar + znear - depth * (zfar - znear));
}

void main()
{
	vec4 opaqueColor 	   = texture(opaqueSceneSampler, inUV);
	vec4 transparencyColor = texture(transparencySampler, inUV);
	vec4 opaqueDepth 	   = texelFetch(opaqueSceneDepthMap, ivec2(inUV.x * constants.width, inUV.y * constants.height),0);
	vec4 transparencyDepth = texture(transparencyLayerDepthMap, inUV);
	float alpha 		   = texture(transparencyAlphaMap, inUV).r;
	vec4 transparencyLightScattering = texture(transparencyLayerLightScatteringSampler, inUV);
	vec4 opaqueSceneLightScattering = texture(opaqueSceneLightScatteringSampler, inUV);
	
	vec4 rgba = vec4(0);
	vec4 lightScattering;
	if (opaqueDepth.r <= transparencyDepth.r){
		rgba = opaqueColor;
		lightScattering = opaqueSceneLightScattering;
	}
	else{
		rgba = transparencyColor * alpha + opaqueColor * (1-alpha);
		lightScattering = transparencyLightScattering;
	}
		
	fragColor_out = rgba;
	lightScattering_out = lightScattering;
}
