#version 430

layout(quads, fractional_odd_spacing, cw) in;

flat in int highDetailTE;
flat in int bezierTE;
in int crackAvoidance;

out vec2 texCoordG;
out int crackAvoidanceG;
flat out int highDetailG;

uniform int bezier;
uniform float texDetail;


// 		-1  3 -3  1
//		 3 -6  3  0
// MB = -3  3  0  0
//		 1  0  0  0
//

const mat4 MB = mat4(vec4(-1.0,3.0,-3.0,1.0), vec4(3.0,-6.0,3.0,0.0), vec4(-3.0,3.0,0.0,0.0), vec4(1.0,0.0,0.0,0.0));


vec2 getTexCoords(float u, float v)
{
	return vec2( u * texDetail, v * texDetail);
}



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
	
	texCoordG = getTexCoords(u,v);
	
	if (bezier == 1 && bezierTE == 1)
			position.xyz = BezierInterpolation();
	
	crackAvoidanceG = crackAvoidance;
	highDetailG = highDetailTE;
	gl_Position = position;
}