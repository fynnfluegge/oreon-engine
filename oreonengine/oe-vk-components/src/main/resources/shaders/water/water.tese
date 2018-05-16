#version 430

layout(quads, fractional_odd_spacing, cw) in;

layout (location = 0) in vec2 inUV[];

layout (location = 0) out vec2 outUV;

layout (push_constant, std430, row_major) uniform Constants{
	mat4 m_World;
	vec2 windDirection;
	float tessSlope;
	float tessShift;
	int tessFactor;
	int uvScale;
	float displacementScale;
	float choppiness;
	int highDetailRange;
	float kReflection;
	float kRefraction;
	int windowWidth;
	int windowHeight;
	float emission;
	float specular;
} constants;

out gl_PerVertex {
	vec4 gl_Position;	
};

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
	
	outUV = uv * constants.uvScale;
	
	gl_Position = position;
}
