#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(quads, fractional_odd_spacing, cw) in;

layout (location = 0) in vec2 inUV[];

layout (location = 0) out vec2 outUV;

layout (push_constant, std430, row_major) uniform Constants{
	mat4 localMatrix;
	mat4 worldMatrix;
	float verticalScaling;
	float horizontalScaling;
	int lod;
	float gap;
	vec2 location;
	vec2 index;
	int lod_morph_area[8];
	int tessFactor;
	float tessSlope;
	float tessShift;
	float uvScaling;
	int highDetailRange;
} constants;

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
	
	position = normalize(position) * constants.horizontalScaling;
	
	// float height = 0;
	// height *= constants.horizontalScaling;
	// position.y = height;
	
	outUV = uv * constants.uvScaling;
	
	gl_Position = vec4(position.xyz,1);
}