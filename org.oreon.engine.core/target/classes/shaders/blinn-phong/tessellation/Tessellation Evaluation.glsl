#version 430

layout(quads, fractional_odd_spacing, cw) in;

in vec2 texCoord2[];

out vec2 texCoord3;

void main(){

    float u = gl_TessCoord.x;
    float v = gl_TessCoord.y;
	
	// world position
	vec4 position =
	((1 - u) * (1 - v) * gl_in[12].gl_Position +
	u * (1 - v) * gl_in[0].gl_Position +
	u * v * gl_in[3].gl_Position +
	(1 - u) * v * gl_in[15].gl_Position);
	
	vec2 texCoord =
	((1 - u) * (1 - v) * texCoord2[12] +
	u * (1 - v) * texCoord2[0] +
	u * v * texCoord2[3] +
	(1 - u) * v * texCoord2[15]);
	
	texCoord3 = texCoord;
	
	gl_Position = position;
}