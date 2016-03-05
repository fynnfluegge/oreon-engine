#version 430 core
#define M_PI 3.1415926535897932384626433832795

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba32f) uniform image2D h0k;

layout (binding = 1, rgba32f) uniform image2D h0kminus;

uniform sampler2D noise_r0;
uniform sampler2D noise_i0;
uniform sampler2D noise_r1;
uniform sampler2D noise_i1;

uniform int N;
uniform int L;
uniform float A;
uniform vec2 w;
uniform float l;

const float g = 9.81;

// Box-Muller-Method

vec2 gaussRND0()
{	
	vec2 texCoord = vec2(gl_GlobalInvocationID.xy)/float(N);
	
	float noise00 = clamp(texture(noise_r0, texCoord).r + 0.00001, 0, 1);
	float noise01 = clamp(texture(noise_i0, texCoord).r + 0.00001, 0, 1);
	
	float u = 2.0*M_PI*noise00;
	float v = sqrt(-2.0 * log(noise01));
	
	vec2 rnd = vec2(v * cos(u), v * sin(u));
	
	return rnd;
}

vec2 gaussRND1()
{	
	vec2 texCoord = vec2(gl_GlobalInvocationID.xy)/float(N);
	
	float noise00 = clamp(texture(noise_r1, texCoord).r + 0.00001, 0, 1);
	float noise01 = clamp(texture(noise_i1, texCoord).r + 0.00001, 0, 1);
	
	float u = 2.0*M_PI*noise00;
	float v = sqrt(-2.0 * log(noise01));
	
	vec2 rnd = vec2(v * cos(u), v * sin(u));
	
	return rnd;
}


void main(void)
{
	vec2 x = vec2(gl_GlobalInvocationID.xy);
	vec2 k = vec2(2.0 * M_PI * x.x/L, 2.0 * M_PI * x.y/L);
	vec2 k_norm = normalize(k);
	float mag = length(k);
	if (mag < 0.0001) mag = 0.0001;
	float magSq = mag * mag;
	float dir = dot(k_norm,w);
	float dir_ = dot(-k_norm,w);
	float h0 = clamp(sqrt((A/(magSq*magSq)) * pow(dir, 2.0) * exp(-(1.0/(magSq * l * l))) * exp(-magSq*pow(l/2000.0,2)))/ sqrt(2.0), 0, 1000000);
	
	imageStore(h0k, ivec2(gl_GlobalInvocationID.xy), vec4(gaussRND0() * h0, 0, 1));
	
	imageStore(h0kminus, ivec2(gl_GlobalInvocationID.xy), vec4(gaussRND1() * h0, 0, 1));
}