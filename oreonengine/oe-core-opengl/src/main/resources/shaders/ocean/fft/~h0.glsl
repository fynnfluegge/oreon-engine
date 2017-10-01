#version 430 core
#define M_PI 3.1415926535897932384626433832795

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba32f) writeonly uniform image2D tilde_h0k;

layout (binding = 1, rgba32f) writeonly uniform image2D tilde_h0minusk;

uniform sampler2D noise_r0;
uniform sampler2D noise_i0;
uniform sampler2D noise_r1;
uniform sampler2D noise_i1;

uniform int N;
uniform int L;
uniform float A;
uniform vec2 w;
uniform float windspeed;

const float g = 9.81;

// Box-Muller-Method

vec4 gaussRND()
{	
	vec2 texCoord = vec2(gl_GlobalInvocationID.xy)/float(N);
	
	float noise00 = clamp(texture(noise_r0, texCoord).r, 0.001, 1.0);
	float noise01 = clamp(texture(noise_i0, texCoord).r, 0.001, 1.0);
	float noise02 = clamp(texture(noise_r1, texCoord).r, 0.001, 1.0);
	float noise03 = clamp(texture(noise_i1, texCoord).r, 0.001, 1.0);
	
	float u0 = 2.0*M_PI*noise00;
	float v0 = sqrt(-2.0 * log(noise01));
	float u1 = 2.0*M_PI*noise02;
	float v1 = sqrt(-2.0 * log(noise03));
	
	vec4 rnd = vec4(v0 * cos(u0), v0 * sin(u0), v1 * cos(u1), v1 * sin(u1));
	
	return rnd;
}


void main(void)
{
	vec2 x = vec2(gl_GlobalInvocationID.xy) - float(N)/2.0;
	
	vec2 k = vec2(2.0 * M_PI * x.x/L, 2.0 * M_PI * x.y/L);

	float L_ = (windspeed * windspeed)/g;
	float mag = length(k);
	if (mag < 0.00001) mag = 0.00001;
	float magSq = mag * mag;
	
	//sqrt(Ph(k))/sqrt(2)
	float h0k = clamp(sqrt((A/(magSq*magSq)) * pow(dot(normalize(k), normalize(w)), 8.0) * exp(-(1.0/(magSq * L_ * L_))) * exp(-magSq*pow(L/2000.0,2.0)))/ sqrt(2.0), -4000.0, 4000.0);
	
	//sqrt(Ph(-k))/sqrt(2)
	float h0minusk = clamp(sqrt((A/(magSq*magSq)) * pow(dot(normalize(-k), normalize(w)), 8.0) * exp(-(1.0/(magSq * L_ * L_))) * exp(-magSq*pow(L/2000.0,2.0)))/ sqrt(2.0), -4000.0, 4000.0);
	
	vec4 gauss_random = gaussRND();
	
	imageStore(tilde_h0k, ivec2(gl_GlobalInvocationID.xy), vec4(gauss_random.xy * h0k, 0, 1));
	
	imageStore(tilde_h0minusk, ivec2(gl_GlobalInvocationID.xy), vec4(gauss_random.zw * h0minusk, 0, 1));
}
