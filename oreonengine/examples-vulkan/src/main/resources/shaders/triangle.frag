#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) in vec2 uv;

layout(location = 0) out vec4 outColor;
layout(location = 1) out vec4 outNormal;

layout(set = 1, binding = 0) uniform sampler2D texSampler;

void main() {

    outColor = texture(texSampler, uv);
	outNormal = vec4(1,0,0,1);
}