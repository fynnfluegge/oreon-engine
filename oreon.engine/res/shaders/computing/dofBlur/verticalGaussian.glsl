#version 430 core

layout (local_size_x = 8, local_size_y = 8) in;

layout (binding = 0, rgba8) uniform writeonly image2D sceneSampler;

layout (binding = 1, rgba8) uniform writeonly image2D verticalBlur;

uniform float windowWidth;
uniform float windowHeight;
uniform float gaussianKernel3[3];
uniform float gaussianKernel5[5];
uniform float gaussianKernel7[7];
uniform float gaussianKernel9[9];

float zfar = 10000.0f;
float znear = 0.1f;

void main(void){

	ivec2 texCoord = ivec2(gl_GlobalInvocationID.x, gl_GlobalInvocationID.y);
}