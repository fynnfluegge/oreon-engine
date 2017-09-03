#version 430 core
#define M_PI 3.1415926535897932384626433832795

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba32f) writeonly uniform image2D tilde_hkt_dy; //height displacement

layout (binding = 1, rgba32f) writeonly uniform image2D tilde_hkt_dx; //choppy-x displacement

layout (binding = 2, rgba32f) writeonly uniform image2D tilde_hkt_dz; //choppy-z displacement

layout (binding = 3, rgba32f) readonly uniform image2D tilde_h0k;

layout (binding = 4, rgba32f) readonly uniform image2D tilde_h0minusk;

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
	vec2 x = ivec2(gl_GlobalInvocationID.xy) - float(N)/2;
	
	vec2 k = vec2(2.0 * M_PI * x.x/L, 2.0 * M_PI * x.y/L);
	
	float magnitude = length(k);
	if (magnitude < 0.00001) magnitude = 0.00001;
	
	float w = sqrt(9.81 * magnitude);
	
	complex tilde_h0k 	 	 = complex(imageLoad(tilde_h0k, ivec2(gl_GlobalInvocationID.xy)).r, 
							imageLoad(tilde_h0k, ivec2(gl_GlobalInvocationID.xy)).g);
							
	ivec2 x_inv = ivec2(gl_GlobalInvocationID.xy);
	
	complex tilde_h0minuskconj   = conj(complex(imageLoad(tilde_h0minusk, x_inv).r, 
								imageLoad(tilde_h0minusk, x_inv).g));
		
	float cosinus = cos(w*t);
	float sinus   = sin(w*t);
		
	// euler formula
	complex exp_iwt = complex(cosinus, sinus);
	complex exp_iwt_inv = complex(cosinus, -sinus);
	
	// dy
	complex h_k_t_dy = add(mul(tilde_h0k, exp_iwt), (mul(tilde_h0minuskconj, exp_iwt_inv)));
	
	// dx
	complex dx = complex(0.0,-k.x/magnitude);
	complex h_k_t_dx = mul(dx, h_k_t_dy);
	
	// dz
	complex dy = complex(0.0,-k.y/magnitude);
	complex h_k_t_dz = mul(dy, h_k_t_dy);
		
	imageStore(tilde_hkt_dy, ivec2(gl_GlobalInvocationID.xy), vec4(h_k_t_dy.real, h_k_t_dy.im, 0, 1));
	imageStore(tilde_hkt_dx, ivec2(gl_GlobalInvocationID.xy), vec4(h_k_t_dx.real, h_k_t_dx.im, 0, 1));
	imageStore(tilde_hkt_dz, ivec2(gl_GlobalInvocationID.xy), vec4(h_k_t_dz.real, h_k_t_dz.im, 0, 1));
}
