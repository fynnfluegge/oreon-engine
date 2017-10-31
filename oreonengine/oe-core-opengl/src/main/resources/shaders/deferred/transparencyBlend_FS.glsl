#version 430

in vec2 texCoord1;

layout(location = 0) out vec4 fragColor;

uniform sampler2D opaqueSceneTexture;
uniform sampler2D opaqueSceneDepthMap;
uniform sampler2D transparencyLayer;
uniform sampler2D transparencyLayerDepthMap;

void main()
{
	vec4 opaqueColor 	   = texture(opaqueSceneTexture, texCoord1);
	vec4 transparencyColor = texture(transparencyLayer, texCoord1);
	vec4 opaqueDepth 	   = texture(opaqueSceneDepthMap, texCoord1);
	vec4 transparencyDepth = texture(transparencyLayerDepthMap, texCoord1);
	
	vec4 rgba = mix(opaqueColor,transparencyColor,0);
	
	// if (opaqueDepth.r >= transparencyDepth.r){
		// rgba = opaqueColor;
	// }
	// else{
		// rgba = transparencyColor;
	// }
		
	fragColor = rgba;
}
