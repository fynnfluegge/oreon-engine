#version 430

layout(quads, fractional_odd_spacing, cw) in;

in vec2 texCoord_TE[];

out vec2 texCoord_GS;

uniform int texDetail;


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
	((1 - u) * (1 - v) * texCoord_TE[12] +
	u * (1 - v) * texCoord_TE[0] +
	u * v * texCoord_TE[3] +
	(1 - u) * v * texCoord_TE[15]);
	
	texCoord_GS = texCoord * texDetail;
	
	gl_Position = position;
}
