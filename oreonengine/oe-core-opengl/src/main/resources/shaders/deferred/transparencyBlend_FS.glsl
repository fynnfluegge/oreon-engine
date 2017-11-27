#version 430

in vec2 texCoord1;

layout(location = 0) out vec4 fragColor_out;
layout(location = 1) out vec4 lightScattering_out;

uniform sampler2D opaqueSceneTexture;
uniform sampler2DMS opaqueSceneLightScatteringTexture;
uniform sampler2DMS opaqueSceneDepthMap;
uniform sampler2D transparencyLayer;
uniform sampler2D transparencyLayerLightScatteringTexture;
uniform sampler2D transparencyLayerDepthMap;
uniform sampler2D transparencyAlphaMap;
uniform int width;
uniform int height;
const float zfar = 10000;
const float znear = 0.1;

float linearizeDepth(float depth)
{
	return (2 * znear) / (zfar + znear - depth * (zfar - znear));
}

void main()
{
	vec4 opaqueColor 	   = texture(opaqueSceneTexture, texCoord1);
	vec4 transparencyColor = texture(transparencyLayer, texCoord1);
	vec4 opaqueDepth 	   = texelFetch(opaqueSceneDepthMap, ivec2(texCoord1.x * width, texCoord1.y * height),0);
	vec4 transparencyDepth = texture(transparencyLayerDepthMap, texCoord1);
	float alpha 		   = texture(transparencyAlphaMap, texCoord1).r;
	vec4 transparencyLightScattering = texture(transparencyLayerLightScatteringTexture, texCoord1);
	vec4 opaqueSceneLightScattering = texelFetch(opaqueSceneLightScatteringTexture, ivec2(texCoord1.x * width, texCoord1.y * height),0);
	
	vec4 rgba;
	vec4 lightScattering;
	if (opaqueDepth.r <= transparencyDepth.r){
		rgba = opaqueColor;
		lightScattering = opaqueSceneLightScattering;
	}
	else{
		rgba = transparencyColor + opaqueColor * (1-alpha);
		lightScattering = transparencyLightScattering;
	}
		
	fragColor_out = rgba;
	lightScattering_out = lightScattering;
}
