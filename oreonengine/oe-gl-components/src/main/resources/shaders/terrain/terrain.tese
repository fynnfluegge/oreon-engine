#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(quads, fractional_odd_spacing, cw) in;

layout (location = 0) in vec2 inUV[];

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

uniform sampler2D heightmap;
uniform float yScale;
uniform int reflectionOffset;

void main(){

    float u = gl_TessCoord.x;
    float v = gl_TessCoord.y;
	
	// world position
	vec4 position =
	((1 - u) * (1 - v) * gl_in[12].gl_Position +
	u * (1 - v) * gl_in[0].gl_Position +
	u * v * gl_in[3].gl_Position +
	(1 - u) * v * gl_in[15].gl_Position);
	
	vec2 uv =
	((1 - u) * (1 - v) * inUV[12] +
	u * (1 - v) * inUV[0] +
	u * v * inUV[3] +
	(1 - u) * v * inUV[15]);
				
	vec4 v_heightmap = texture(heightmap, uv).rgba;
	float height = v_heightmap.y * yScale + reflectionOffset;
	float slope = v_heightmap.z;
	
	position.y = height;
	position.x -= slope * v_heightmap.x * yScale;
	position.z -= slope * v_heightmap.z * yScale;
		
	outUV = uv * uvScale;
	
	gl_Position = position;
}