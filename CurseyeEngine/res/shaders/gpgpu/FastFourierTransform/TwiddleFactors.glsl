#version 430 core
#define M_PI 3.1415926535897932384626433832795

layout (local_size_x = 1, local_size_y = 16) in;

layout (binding = 0, rgba32f)writeonly uniform image2D image;

layout (std430, binding = 0) buffer indices {
	int j[];
};

struct complex
{	
	float real;
	float im;
};

uniform int N;

void main(void)
{
	vec2 x = gl_GlobalInvocationID.xy;
	float k = mod(x.y * (float(N)/ pow(2,x.x+1)), N);
	complex twiddle = complex( cos(2.0*M_PI*k/float(N)), - sin(2.0*M_PI*k/float(N)));
	
	int butterflyspan = int(pow(2, x.x));
	
	int butterflyleg;
	
	if (mod(x.y, pow(2, x.x + 1)) < pow(2, x.x))
		butterflyleg = 1;
	else butterflyleg = 0;
	
	if (x.x == 0) {
		// top leg
		if (butterflyleg == 1)
			imageStore(image, ivec2(x), vec4(twiddle.real, twiddle.im, indices.j[int(x.y)], indices.j[int(x.y + 1)]));
		// bot leg
		else	
			imageStore(image, ivec2(x), vec4(twiddle.real, twiddle.im, indices.j[int(x.y - 1)], indices.j[int(x.y)]));
	}
	else {
		// top leg
		if (butterflyleg == 1)
			imageStore(image, ivec2(x), vec4(twiddle.real, twiddle.im, x.y, x.y + butterflyspan));
		// bot leg
		else
			imageStore(image, ivec2(x), vec4(twiddle.real, twiddle.im, x.y - butterflyspan, x.y));
	}
}