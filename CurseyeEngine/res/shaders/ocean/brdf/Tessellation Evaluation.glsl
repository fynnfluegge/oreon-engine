#version 430

layout(quads, fractional_odd_spacing, cw) in;

in vec2 texCoordTE[];

out vec2 texCoordG;

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
	((1 - u) * (1 - v) * texCoordTE[12] +
	u * (1 - v) * texCoordTE[0] +
	u * v * texCoordTE[3] +
	(1 - u) * v * texCoordTE[15]);
	
	texCoordG = texCoord * texDetail;
	
	gl_Position = position;
}