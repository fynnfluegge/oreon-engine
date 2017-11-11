#version 430

layout (local_size_x = 16, local_size_y = 16) in;

layout (binding = 0, rgba32f) uniform writeonly image2D ssaoBlur_out;

layout (binding = 1, rgba32f) uniform readonly image2D ssao_in;

uniform int uBlurSize = 4; // use size of noise texture

out float fResult;

void main() {

	ivec2 computeCoord = ivec2(gl_GlobalInvocationID.xy);

	float result = 0.0;
	vec2 hlim = vec2(float(-uBlurSize) * 0.5 + 0.5);
	for (int i = 0; i < uBlurSize; ++i) {
		for (int j = 0; j < uBlurSize; ++j) {
			vec2 offset = (hlim + vec2(float(x), float(y))) * texelSize;
			result += texture(uTexInput, vTexcoord + offset).r;
		}
	}
 
   fResult = result / float(uBlurSize * uBlurSize);
}