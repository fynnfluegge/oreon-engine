#version 430

layout(quads, fractional_odd_spacing, cw) in;

in vec2 texCoord2[];
flat in int tessellation;
out vec2 texCoordG;

struct Fractal
{
	sampler2D heightmap;
};

uniform Fractal fractals[10];
uniform sampler2D heightmap;
uniform float scaleY;
uniform int bezier;
uniform float texDetail;


// 		-1  3 -3  1
//		 3 -6  3  0
// MB = -3  3  0  0
//		 1  0  0  0
//

const mat4 MB = mat4(vec4(-1.0,3.0,-3.0,1.0), vec4(3.0,-6.0,3.0,0.0), vec4(-3.0,3.0,0.0,0.0), vec4(1.0,0.0,0.0,0.0));


vec3 BezierInterpolation()
{
	float u = gl_TessCoord.x;
	float v = gl_TessCoord.y;
	
	vec4 U = vec4(u*u*u, u*u, u, 1);
	vec4 V = vec4(v*v*v, v*v, v, 1); 
	
	mat4 GBY = mat4(vec4(gl_in[12].gl_Position.y, gl_in[8].gl_Position.y,  gl_in[4].gl_Position.y, gl_in[0].gl_Position.y),
					vec4(gl_in[13].gl_Position.y, gl_in[9].gl_Position.y,  gl_in[5].gl_Position.y, gl_in[1].gl_Position.y),
					vec4(gl_in[14].gl_Position.y, gl_in[10].gl_Position.y, gl_in[6].gl_Position.y, gl_in[2].gl_Position.y),
					vec4(gl_in[15].gl_Position.y, gl_in[11].gl_Position.y, gl_in[7].gl_Position.y, gl_in[3].gl_Position.y));
					
	mat4 GBX = mat4(vec4(gl_in[12].gl_Position.x, gl_in[8].gl_Position.x,  gl_in[4].gl_Position.x, gl_in[0].gl_Position.x),
					vec4(gl_in[13].gl_Position.x, gl_in[9].gl_Position.x,  gl_in[5].gl_Position.x, gl_in[1].gl_Position.x),
					vec4(gl_in[14].gl_Position.x, gl_in[10].gl_Position.x, gl_in[6].gl_Position.x, gl_in[2].gl_Position.x),
					vec4(gl_in[15].gl_Position.x, gl_in[11].gl_Position.x, gl_in[7].gl_Position.x, gl_in[3].gl_Position.x));
	
	mat4 GBZ = mat4(vec4(gl_in[12].gl_Position.z, gl_in[8].gl_Position.z,  gl_in[4].gl_Position.z, gl_in[0].gl_Position.z),
					vec4(gl_in[13].gl_Position.z, gl_in[9].gl_Position.z,  gl_in[5].gl_Position.z, gl_in[1].gl_Position.z),
					vec4(gl_in[14].gl_Position.z, gl_in[10].gl_Position.z, gl_in[6].gl_Position.z, gl_in[2].gl_Position.z),
					vec4(gl_in[15].gl_Position.z, gl_in[11].gl_Position.z, gl_in[7].gl_Position.z, gl_in[3].gl_Position.z));
	
					 
	mat4 cx = MB * GBX * transpose(MB);
	mat4 cy = MB * GBY * transpose(MB);
	mat4 cz = MB * GBZ * transpose(MB);
	
	float x = dot(cx * V, U);
	float y = dot(cy * V, U);
	float z = dot(cz * V, U);
	
	return vec3(x,y,z);
}



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
	

	float height =   (
					  texture(heightmap, texCoord).r
					+ texture(fractals[0].heightmap, texCoord).r
					+ texture(fractals[1].heightmap, texCoord*2).r *1.4
					+ texture(fractals[2].heightmap, texCoord*4).r *0.4
					+ texture(fractals[3].heightmap, texCoord*6).r *0.2
					+ texture(fractals[4].heightmap, texCoord*10).r *0.08
					+ texture(fractals[5].heightmap, texCoord*20).r *0.02
					+ texture(fractals[6].heightmap, texCoord*22).r *0.02
					) * scaleY;
					
	position.y = height;

	if (bezier == 1)
		position.xyz = BezierInterpolation();
		
	texCoordG = texCoord * texDetail;
	
	gl_Position = position;
}