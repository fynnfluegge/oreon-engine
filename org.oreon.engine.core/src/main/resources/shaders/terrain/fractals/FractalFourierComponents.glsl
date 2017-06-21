#version 430 core
#define M_PI 3.1415926535897932384626433832795

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba32f) uniform image2D Dy;

layout (binding = 3, rgba32f) uniform image2D h0k;

layout (binding = 4, rgba32f) uniform image2D h0kminus;

uniform int L;
uniform float t;

struct complex
{	
	float real;
	float im;
};

complex mul(complex c0, complex c1)
{
	complex c;
	c.real = c0.real * c1.real - c0.im * c1.im;
	c.im   = c0.real * c1.im + c0.im * c1.real;
	return c;
}

complex add(complex c0, complex c1)
{
	complex c;
	c.real = c0.real + c1.real;
	c.im   = c0.im   + c1.im;
	return c;
}

complex conj(complex c)
{
	complex c_conj = complex(c.real, -c.im);
	
	return c_conj;
}

void main(void)
{
	vec2 x = ivec2(gl_GlobalInvocationID.xy);
	
	vec2 k = vec2(2.0 * M_PI * x.x/L, 2.0 * M_PI * x.y/L);
	
	float magnitude = length(k);
	if (magnitude < 0.0001) magnitude = 0.0001;
	
	float w = sqrt(9.81 * magnitude);
	
	complex h0 	 	 = complex(imageLoad(h0k, ivec2(gl_GlobalInvocationID.xy)).r, imageLoad(h0k, ivec2(gl_GlobalInvocationID.xy)).g);
	
	complex h0conj   = conj(complex(imageLoad(h0kminus, ivec2(gl_GlobalInvocationID.xy)).r, imageLoad(h0kminus, ivec2(gl_GlobalInvocationID.xy)).g));
		
	float cosinus = cos(w*t);
	float sinus   = sin(w*t);
		
	complex exp_iwt = complex(cosinus, sinus);
	complex exp_iwt_inv = complex(cosinus, -sinus);
		
	complex h_k_t = add(mul(h0, exp_iwt), (mul(h0conj, exp_iwt_inv)));
	
		
	imageStore(Dy, ivec2(gl_GlobalInvocationID.xy), vec4(h_k_t.real, h_k_t.im, 0, 1));
}