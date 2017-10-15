#version 430

layout (location = 0) in vec2 position0;

out vec2 texCoord1;

struct Fractal
{
	sampler2D heightmap;
	int scaling;
	float strength;
};

layout (std140, row_major) uniform Camera{
	vec3 eyePosition;
	mat4 m_View;
	mat4 m_ViewProjection;
	vec4 frustumPlanes[6];
};

uniform sampler2D heightmap;
uniform Fractal fractals0[1];
uniform float scaleY;
uniform int lod;
uniform vec2 index;
uniform mat4 localMatrix;
uniform mat4 worldMatrix;
uniform float gap;
uniform vec2 location;

uniform int lod_morph_area[8];

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

vec2 morph(vec2 localPosition, int morph_area){

	vec2 morphing = vec2(0,0);
	
	vec2 fixPointLatitude;
	vec2 fixPointLongitude;float distLatitude;
	float distLongitude;
	
	if(index == vec2(0,0)){
		fixPointLatitude = location + vec2(gap,0);
		fixPointLongitude = location + vec2(0,gap);
	}
	else if(index == vec2(1,0)){
		fixPointLatitude = location;
		fixPointLongitude = location + vec2(gap,gap);
	}
	else if(index == vec2(0,1)){
		fixPointLatitude = location + vec2(gap,gap);
		fixPointLongitude = location;
	}
	else if(index == vec2(1,1)){
		fixPointLatitude = location + vec2(0,gap);
		fixPointLongitude = location + vec2(gap,0);
	}
	
	float planarFactor;
	if (eyePosition.y > abs(scaleY))
		planarFactor = 1;
	else
		planarFactor = eyePosition.y/ abs(scaleY);
	
	distLatitude = length(eyePosition - (worldMatrix *
			vec4(fixPointLatitude.x,planarFactor,fixPointLatitude.y,1)).xyz);
	distLongitude = length(eyePosition - (worldMatrix *
			vec4(fixPointLongitude.x,planarFactor,fixPointLongitude.y,1)).xyz);
			
	if (distLatitude > morph_area)
			morphing.x = morphLatitude(localPosition.xy);
	if (distLongitude > morph_area)
		morphing.y = morphLongitude(localPosition.xy);
		
	return morphing;
}

void main()
{
	vec2 localPosition = (localMatrix * vec4(position0.x,0,position0.y,1)).xz;
	
	localPosition += morph(localPosition,lod_morph_area[lod-1]);
	
	texCoord1 = localPosition;
	float height = 0;

	height += texture(heightmap, localPosition).r;
	height += texture(fractals0[0].heightmap, texCoord1*fractals0[0].scaling).r * fractals0[0].strength;
					
	gl_Position = worldMatrix * vec4(localPosition.x,height,localPosition.y,1);
}