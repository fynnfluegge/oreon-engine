#version 450
#extension GL_ARB_separate_shader_objects : enable

#lib.glsl

layout (location = 0) in vec2 inPosition;

layout (location = 0) out vec2 outUV;

layout (std430, binding = 1) buffer ssbo0 {
	vec3 fogColor;
	float sightRangeFactor;
	int diamond_square_enable;
	int tessFactor;
	float tessSlope;
	float tessShift;
	float xzScale;
	int isBezier;
	float uvScale;
	int largeDetailRange;
};

layout (std430, binding = 2) buffer ssbo1 {
	int lod_morph_area[];
};

uniform mat4 localMatrix;
uniform mat4 worldMatrix;
uniform int lod;
uniform vec2 index;
uniform float gap;
uniform vec2 location;
uniform sampler2D heightmap;

float morphLatitude(vec2 position)
{
	vec2 frac = position - location;
	
	if (index == vec2(0,0)){
		float morph = frac.x - frac.y;
		if (morph > 0)
			return morph;
	}
	if (index == vec2(1,0)){
		float morph = gap - frac.x - frac.y;
		if (morph > 0)
			return morph;
	}
	if (index == vec2(0,1)){
		float morph = frac.x + frac.y - gap;
		if (morph > 0)
			return -morph;
	}
	if (index == vec2(1,1)){
		float morph = frac.y - frac.x;
		if (morph > 0)
			return -morph;
	}
	return 0;
}

float morphLongitude(vec2 position)
{
	vec2 frac = position - location;
	
	if (index == vec2(0,0)){
		float morph = frac.y - frac.x;
		if (morph > 0)
			return -morph;
	}
	else if (index == vec2(1,0)){
		float morph = frac.y - (gap - frac.x);
		if (morph > 0)
			return morph;
	}
	else if (index == vec2(0,1)){
		float morph = gap - frac.y - frac.x;
		if (morph > 0)
			return -morph;
	}
	else if (index == vec2(1,1)){
		float morph = frac.x - frac.y;
		if (morph > 0)
			return morph;
	}
	return 0;
}

vec2 diamondSquare(vec2 localPosition, float height, int morph_area){
	
	vec2 morphing = vec2(0,0);
	
	vec2 fixPointLatitude = vec2(0,0);
	vec2 fixPointLongitude = vec2(0,0);
	float distLatitude;
	float distLongitude;
	
	if (index == vec2(0,0)) {
		fixPointLatitude = location + vec2(gap,0);
		fixPointLongitude = location + vec2(0,gap);
	}
	else if (index == vec2(1,0)) {
		fixPointLatitude = location;
		fixPointLongitude = location + vec2(gap,gap);
	}
	else if (index == vec2(0,1)) {
		fixPointLatitude = location + vec2(gap,gap);
		fixPointLongitude = location;
	}
	else if (index == vec2(1,1)) {
		fixPointLatitude = location + vec2(0,gap);
		fixPointLongitude = location + vec2(gap,0);
	}
		
	distLatitude = length(camera.eyePosition - (worldMatrix * 
					vec4(fixPointLatitude.x,height,fixPointLatitude.y,1)).xyz);
	distLongitude = length(camera.eyePosition - (worldMatrix * 
					vec4(fixPointLongitude.x,height,fixPointLongitude.y,1)).xyz);
					
	if (distLatitude > morph_area)
		morphing.x = morphLatitude(localPosition.xy);
	if (distLongitude > morph_area)
		morphing.y = morphLongitude(localPosition.xy);
		
	return morphing;
}

void main()
{
	vec2 localPosition = (localMatrix * vec4(inPosition.x,0,inPosition.y,1)).xz;
	
	float height = texture(heightmap, localPosition).y;
	
	if (diamond_square_enable == 1){
		if (lod > 0)
		localPosition += diamondSquare(localPosition,height,lod_morph_area[lod-1]);
	}
	
	height = texture(heightmap, localPosition).y;
	
	outUV = localPosition;
					
	gl_Position = worldMatrix * vec4(localPosition.x,height,localPosition.y,1);
}