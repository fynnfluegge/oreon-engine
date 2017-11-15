#version 430

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba32f) uniform writeonly image2D ssaoBlur_out;

layout (binding = 1, rgba32f) uniform readonly image2D ssao_in;

uniform int width = 1280;
uniform int height = 720;

const int uBlurSize = 4;

void main() {

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.xy);

	float kernelSize = float(uBlurSize * uBlurSize);
	
	float result = 0.0;
	ivec2 hlim = ivec2(-uBlurSize/2.0, -uBlurSize/2.0);
	
	for (int i = 0; i < uBlurSize; ++i) {
		for (int j = 0; j < uBlurSize; ++j) {
			ivec2 offset = hlim + ivec2(i,j);
			ivec2 sampleCoord = computeCoord + offset;
			if (sampleCoord.x < width && sampleCoord.y < height && sampleCoord.x >= 0 && sampleCoord.y >= 0){		
				result += imageLoad(ssao_in, computeCoord + offset).r;
			}
			else{
				kernelSize--;
			}
		}
	}
 
	result = result / kernelSize;
	imageStore(ssaoBlur_out, computeCoord, vec4(result,result,result,1.0));
}