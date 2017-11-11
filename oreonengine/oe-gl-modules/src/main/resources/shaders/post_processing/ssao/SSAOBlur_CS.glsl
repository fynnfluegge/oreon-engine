#version 430

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba32f) uniform writeonly image2D ssaoBlur_out;

layout (binding = 1, rgba32f) uniform readonly image2D ssao_in;

const int uBlurSize = 8;

void main() {

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.xy);

	float result = 0.0;
	ivec2 hlim = ivec2(-uBlurSize/2.0, -uBlurSize/2.0);
	for (int i = 0; i < uBlurSize; ++i) {
		for (int j = 0; j < uBlurSize; ++j) {
			ivec2 offset = hlim + ivec2(i,j);
			result += imageLoad(ssao_in, computeCoord + offset).r;
		}
	}
 
	result = result / float(uBlurSize * uBlurSize);
   imageStore(ssaoBlur_out, computeCoord, vec4(result,result,result,1.0));
}