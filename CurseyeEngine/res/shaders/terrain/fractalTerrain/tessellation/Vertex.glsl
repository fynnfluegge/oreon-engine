#version 430

layout (location = 0) in vec2 position0;

out vec2 texCoord1;

struct Fractal
{
	sampler2D heightmap;
};

uniform Fractal fractals[10];
uniform float scaleY;
uniform int lod;
uniform vec2 index;
uniform mat4 worldMatrix;
uniform float gap;
uniform vec2 location;
uniform vec3 eyePosition;

const int lod1_morph_area = 1700;
const int lod2_morph_area = 1250;
const int lod3_morph_area = 725;
const int lod4_morph_area = 360;
const int lod5_morph_area = 80;
const int lod6_morph_area = 40;
const int lod7_morph_area = 0;

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
	if (index == vec2(1,0)){
		float morph = frac.y - (gap - frac.x);
		if (morph > 0)
			return morph;
	}
	if (index == vec2(0,1)){
		float morph = gap - frac.y - frac.x;
		if (morph > 0)
			return -morph;
	}
	if (index == vec2(1,1)){
		float morph = frac.x - frac.y;
		if (morph > 0)
			return morph;
	}
	return 0;
}

vec2 morph(int morph_area){

	vec2 morphing;
	
	vec2 fixPointLatitude;
	vec2 fixPointLongitude;
	float distLatitude;
	float distLongitude;
	
	if(index == vec2(0,0)){
		fixPointLatitude = location + vec2(gap,0);
		fixPointLongitude = location + vec2(0,gap);
	}
	if(index == vec2(1,0)){
		fixPointLatitude = location;
		fixPointLongitude = location + vec2(gap,gap);
	}
	if(index == vec2(0,1)){
		fixPointLatitude = location + vec2(gap,gap);
		fixPointLongitude = location;
	}
	if(index == vec2(1,1)){
		fixPointLatitude = location + vec2(0,gap);
		fixPointLongitude = location + vec2(gap,0);
	}
	
	float planarFactor;
	if (eyePosition.y > scaleY)
		planarFactor = 1;
	else planarFactor = eyePosition.y/scaleY;
	
	distLatitude = length(eyePosition - (worldMatrix *
			vec4(fixPointLatitude.x,planarFactor,fixPointLatitude.y,1)).xyz);
	distLongitude = length(eyePosition - (worldMatrix *
			vec4(fixPointLongitude.x,planarFactor,fixPointLongitude.y,1)).xyz);
			
	if (distLatitude > morph_area)
			morphing.x = morphLatitude(position0.xy);
	if (distLongitude > morph_area)
		morphing.y = morphLongitude(position0.xy);
		
	return morphing;
}

void main()
{
	vec2 vertex = position0.xy;
	
	if (lod == 1){
		vertex += morph(lod1_morph_area);
	}
	if (lod == 2){
		vertex += morph(lod2_morph_area);
	}
	if (lod == 3){
		vertex += morph(lod3_morph_area);
	}
	if (lod == 4){
		vertex += morph(lod4_morph_area);
	}
	if (lod == 5){
		vertex += morph(lod5_morph_area);
	}
	if (lod == 6){
		vertex += morph(lod6_morph_area);
	}
	if (lod == 7){
		vertex += morph(lod7_morph_area);
	}
	
	texCoord1 = vertex;
	float height = 	  texture(fractals[0].heightmap, texCoord1).r
					+ texture(fractals[1].heightmap, texCoord1*2).r * 1.4
					+ texture(fractals[2].heightmap, texCoord1*4).r * 0.4
					+ texture(fractals[3].heightmap, texCoord1*6).r * 0.2
					+ texture(fractals[4].heightmap, texCoord1*10).r *0.08
					+ texture(fractals[5].heightmap, texCoord1*20).r *0.02
					+ texture(fractals[6].heightmap, texCoord1*22).r *0.02;
					
	gl_Position = worldMatrix * vec4(vertex.x,height,vertex.y,1);
}